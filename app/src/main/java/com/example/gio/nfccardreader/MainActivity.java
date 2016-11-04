package com.example.gio.nfccardreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;


public class MainActivity extends AppCompatActivity implements CardNfcAsyncTask.CardNfcInterface {

    public static final String TAG = "MainActivity";

    private Toolbar toolbar;

    private CardNfcAsyncTask mCardNfcAsyncTask;

    private NfcAdapter myNfcAdapter;
    private TextView tvCardNumber;
    private TextView tvExpiredDate;
    private ImageView ivLogo;
    private ConstraintLayout llInfo;
    private ImageView ivNfc;
    private LinearLayout llLogo;


    private boolean mIntentFromCreate;


    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (myNfcAdapter == null) {
            Toast.makeText(this, "Device doesnot support NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!myNfcAdapter.isEnabled()) {
            Toast.makeText(this, getResources().getString(R.string.NFC_turned_off), Toast.LENGTH_SHORT).show();
            showSnackBar(getResources().getString(R.string.NFC_turned_off));

        } else {
            initView();
            mIntentFromCreate = true;
            createProgressDialog();
            onNewIntent(getIntent());

        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

//            Toast.makeText(this, "2sds", Toast.LENGTH_SHORT).show(); // piradoba

        mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate)
                .build();

    }

    @Override
    public void cardIsReadyToRead() {
        llLogo.setVisibility(View.GONE);
        llInfo.setVisibility(View.VISIBLE);
        String card = mCardNfcAsyncTask.getCardNumber();
        card = getPrettyCardNumber(card);
        String expiredDate = mCardNfcAsyncTask.getCardExpireDate();
        String cardType = mCardNfcAsyncTask.getCardType();
        tvCardNumber.setText(card);
        tvExpiredDate.setText(expiredDate);
        parseCardType(cardType);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIntentFromCreate = false;

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void initView() {
        tvCardNumber = (TextView) findViewById(R.id.tvCardNumber);
        tvExpiredDate = (TextView) findViewById(R.id.tvExpiredDate);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        llInfo = (ConstraintLayout) findViewById(R.id.llInfo);
        ivNfc = (ImageView) findViewById(R.id.ivNfc);
        llLogo = (LinearLayout) findViewById(R.id.llLogo);
    }

    //=========================================================================

    @Override
    public void startNfcReadCard() {
        progressDialog.show();

    }


    @Override
    public void doNotMoveCardSoFast() {

    }

    @Override
    public void unknownEmvCard() {

    }

    @Override
    public void cardWithLockedNfc() {

    }

    @Override
    public void finishNfcReadCard() {
        progressDialog.dismiss();
    }


    //=========================================================================

    private void createProgressDialog() {
        String title = "scannign";
        String mess = "Please do not remove or move card during reading";
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(title);
        progressDialog.setMessage(mess);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    private void parseCardType(String cardType) {
        Toast.makeText(this, cardType.toString(), Toast.LENGTH_SHORT).show();

        if (cardType.equals(CardNfcAsyncTask.CARD_NAB_VISA) || cardType.equals(CardNfcAsyncTask.CARD_VISA)) {
            ivLogo.setImageResource(R.mipmap.visa_logo);
        } else if (cardType.equals(CardNfcAsyncTask.CARD_MASTER_CARD)) {
            ivLogo.setImageResource(R.mipmap.master_logo);
        } else {
            Toast.makeText(this, "unnon card", Toast.LENGTH_SHORT).show();
//            unknownEmvCard();
        }
    }

    private String getPrettyCardNumber(String card) {
        String div = " - ";
        return card.substring(0, 4) + div + card.substring(4, 8) + div + card.substring(8, 12)
                + div + card.substring(12, 16);
    }

    private void showSnackBar(String message) {
        Snackbar.make(toolbar, message, Snackbar.LENGTH_SHORT).show();
    }

}
