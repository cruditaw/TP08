package com.example.cdsm.tp08;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String txtMessage;
    private String txtPhoneNo;

    private final int RESULT_PICK_CONTACT = 321;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    private boolean retrieveViewData() {
        txtMessage = ((EditText) findViewById(R.id.editText)).getText().toString();
        txtPhoneNo = ((EditText) findViewById(R.id.editTel)).getText().toString();

        if (txtPhoneNo != null && txtMessage != null) {
            if (txtPhoneNo.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage("NUMERO  DE TEL OBLIGATOIRE !!!!!!!!!!!!!!!!!");
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }

            if (txtMessage.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage("MESSAGE OBLIGATOIRE !!!!!!!!!!!!!!!!!");
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    public void onCallClick(View view) {
        if (retrieveViewData()) {
            String myData = "tel:" + txtPhoneNo;
            Intent myActivity = new Intent(Intent.ACTION_DIAL, Uri.parse(myData));
            startActivity(myActivity);
        }
    }

    public void onDirectCallClick(View view) {
        if (retrieveViewData()) {
            String myData = "tel:" + txtPhoneNo;
            Intent myActivity = new Intent(Intent.ACTION_CALL, Uri.parse(myData));
            try {
                startActivity(myActivity);
            } catch(SecurityException se){
                Toast.makeText(this, "App doesn't have required permissions for a direct call !", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void onInternetClick(View view) {
        if (retrieveViewData()) {
            String myData = "http://" + txtMessage;
            Intent myActivity2 = new Intent(Intent.ACTION_VIEW, Uri.parse(myData));
            startActivity(myActivity2);
        }
    }

    public void onSendClick(View view) {
        if (retrieveViewData()) {
            String dest = "smsto:" + txtMessage;
            Intent myActivity3 = new Intent(Intent.ACTION_SENDTO, Uri.parse(dest));
            myActivity3.putExtra("sms_body", txtMessage);
            startActivity(myActivity3);
        }  
    }

    public void onDirectSendClick(View view) {
        if (retrieveViewData()) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(txtPhoneNo, null, txtMessage, null, null);
                Toast.makeText(getApplicationContext(), "Sms successfully send !", Toast.LENGTH_SHORT).show();
            } catch (SecurityException se) {
                Toast.makeText(getApplicationContext(), "Error ! Sms not send !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onContactsClick(View view) {
        if (retrieveViewData()) {
            String myData = "content://contacts/people/";
            Intent myActivity4 = new Intent(Intent.ACTION_VIEW, Uri.parse(myData));
            startActivity(myActivity4);
        }
    }

    public void onChooseContactClick(View view) {
        if (retrieveViewData()) {
            Intent contactsPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(contactsPickerIntent, RESULT_PICK_CONTACT);
        }
    }

    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == RESULT_OK) {
            switch (reqCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
                default:
                    break;
            }
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {

            String phoneNo = null;
            String name = null;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();

            int phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            phoneNo = cursor.getString(phoneIdx);
            name = cursor.getString(nameIdx);

            txtMessage = name;
            txtPhoneNo = phoneNo;

        } catch(Exception e) {
            System.out.println("-- Exception on MainActivity.contactPicked(Intent) call : ");
            e.printStackTrace();
        }
    }
}
