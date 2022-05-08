package eu.ase.ro.grupa1086.licentamanolachemariacatalina.card;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.ConfirmationOrder;

public class CardPayment extends AppCompatActivity {

    CardForm cardForm;
    Button btnBuy;
    AlertDialog.Builder alertBuilder;
    String orderId;
    String origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        if (getIntent() != null && getIntent().getExtras() != null) {
            origin = getIntent().getExtras().getString("origin");
            if (origin != null && (origin.equals("cardPayment"))) {
                orderId = getIntent().getStringExtra("orderId");
            }
            if (origin != null && (origin.equals("cardPayment-anotherAddress"))) {
                orderId = getIntent().getStringExtra("orderId");
            }
        }

        cardForm = findViewById(R.id.cardForm);
        btnBuy = findViewById(R.id.btnBuy);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .mobileNumberRequired(true)
                .setup(CardPayment.this);
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardForm.isValid()) {
                    alertBuilder = new AlertDialog.Builder(new ContextThemeWrapper(CardPayment.this, R.style.AlertDialogStyle));
                    alertBuilder.setTitle("Confirmare inainte de plata");
                    alertBuilder.setMessage("Numar card: " + cardForm.getCardNumber() + "\n" +
                            "Data expirarii: " + cardForm.getExpirationDateEditText().getText().toString() + "\n" +
                            "CVV: " + cardForm.getCvv() + "\n" +
                            "Numar de telefon: " + cardForm.getMobileNumber());
                    alertBuilder.setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(CardPayment.this, "Plata a fost efectuata", Toast.LENGTH_LONG).show();
                            Intent confirmationOrder = new Intent(CardPayment.this, ConfirmationOrder.class);
                            confirmationOrder.putExtra("orderId", orderId);
                            if(origin.equals("cardPayment")) {
                                confirmationOrder.putExtra("origin", "cardPayment");
                            } else if(origin.equals("cardPayment-anotherAddress")) {
                                confirmationOrder.putExtra("origin", "cardPayment-anotherAddress");
                            }
                            startActivity(confirmationOrder);
                            finish();
                        }
                    });
                    alertBuilder.setNegativeButton("Anulare", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(CardPayment.this, "Toate campurile sunt necesare pentru procesarea platii", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}