package com.duzceuni.denemeapplication.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.duzceuni.denemeapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.vertexai.FirebaseVertexAI;
import com.google.firebase.vertexai.GenerativeModel;
import com.google.firebase.vertexai.java.GenerativeModelFutures;
import com.google.firebase.vertexai.type.Content;
import com.google.firebase.vertexai.type.GenerateContentResponse;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class AskAIActivity extends AppCompatActivity {

    private ImageButton imgBtnReturn, btnSend;
    private TextView geminiAIResponse;
    private EditText txtAsk;
    private ProgressBar progressBar;

    GenerativeModel gm = FirebaseVertexAI.getInstance().generativeModel("gemini-2.0-flash");
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_ai);

        initComponents();
        registerEventHandlers();
    }

    private void initComponents() {
        imgBtnReturn = findViewById(R.id.imgBtnReturn);
        btnSend = findViewById(R.id.btnSend);
        geminiAIResponse = findViewById(R.id.geminiAIResponse);
        txtAsk = findViewById(R.id.txtAsk);
        progressBar = findViewById(R.id.progressBar);
    }

    private void registerEventHandlers() {
        askAI();
        returnToHome();
    }

    private void askAI() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userPrompt = txtAsk.getText().toString().trim();

                if (userPrompt.isEmpty()) {
                    geminiAIResponse.setText("Lütfen bir soru girin.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                geminiAIResponse.setText("");

                Content prompt = new Content.Builder()
                        .addText("Sen bir kütüphane asistanısın. Sadece kitaplarla ilgili konularda bilgi ver." +
                                " Kitap türleri, yazarlar, özetler, öneriler gibi konular hakkında konuşabilirsin " +
                                "Konu dışı sorulara 'Bu konuda yardımcı olamıyorum.' yanıtını ver.\n\n" +
                                "Aşağıdaki kurallara göre kitap tavsiyesi yap:\n" +
                                "- Eğer kullanıcı tür belirtirse, ona uygun popüler bir kitap öner.\n" +
                                "- Eğer tür belirtilmezse, rastgele ama ilgi çekici bir kitap öner.\n" +
                                "- Kitap önerirken kitabın adı, yazarı ve türünü mutlaka belirt.\n" +
                                "- Ek açıklama gerekiyorsa kısa ve öz tut.\n\n" +
                                "Kullanıcının sorusu:\n")
                        .addText(userPrompt)
                        .build();

                Publisher<GenerateContentResponse> streamingResponse = model.generateContentStream(prompt);

                final String[] fullResponse = {""};

                streamingResponse.subscribe(new Subscriber<GenerateContentResponse>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(GenerateContentResponse generateContentResponse) {
                        String chunk = generateContentResponse.getText();
                        fullResponse[0] += chunk;
                        runOnUiThread(() -> geminiAIResponse.setText(fullResponse[0]));
                    }

                    @Override
                    public void onComplete() {
                        runOnUiThread(() -> {
                            // Cevaptan kitap adını tahmin et (basitçe tüm cevabı geçiriyoruz)
                            checkBookInDatabase(fullResponse[0]);
                        });
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            geminiAIResponse.setText("Bir hata oluştu. Lütfen tekrar deneyin.");
                        });
                    }
                });
            }
        });
    }

    private void checkBookInDatabase(String aiResponse) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("books");

        // AI cevabından bir kitap adı bulmak için örnek regex veya basit analiz yapılabilir.
        // Bu örnekte basit olması için AI cevabının tamamı kitap adı gibi kabul ediliyor.
        // Geliştirmek istersen: kitap adı tespiti için NLP ya da string analizi ekleyebilirsin.

        String possibleTitle = aiResponse.split("\n")[0]; // İlk satırı al
        DatabaseReference booksRef = database.orderByChild("title").equalTo(possibleTitle).getRef();

        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    geminiAIResponse.append("\n\n✅ Bu kitap veritabanında mevcut.");
                } else {
                    geminiAIResponse.append("\n\n⚠️ Bu kitap veritabanında bulunamadı. Ama işte önerim:");
                    suggestAlternativeBook(); // alternatif öneri getir
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                geminiAIResponse.setText("Veritabanı hatası: " + databaseError.getMessage());
            }
        });
    }

    private void suggestAlternativeBook() {
        Content altPrompt = new Content.Builder()
                .addText("Kullanıcının aradığı kitap veritabanında bulunamadı. Benzer türde veya popüler bir kitap önerisi yap.")
                .build();

        Publisher<GenerateContentResponse> altStream = model.generateContentStream(altPrompt);

        final StringBuilder altSuggestion = new StringBuilder();

        altStream.subscribe(new Subscriber<GenerateContentResponse>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(GenerateContentResponse response) {
                altSuggestion.append(response.getText());
                runOnUiThread(() -> geminiAIResponse.append("\n📚 " + response.getText()));
            }

            @Override
            public void onComplete() {}

            @Override
            public void onError(Throwable t) {
                runOnUiThread(() -> geminiAIResponse.append("\n❌ Alternatif kitap önerilirken hata oluştu."));
            }
        });
    }

    private void returnToHome() {
        imgBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnToMain = new Intent(AskAIActivity.this, MainActivity.class);
                startActivity(returnToMain);
            }
        });
    }
}
