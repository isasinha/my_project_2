package com.example.my_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private RecyclerView mensagensRecyclerView;
    private MensagemAdapter adapter;
    private List<Mensagem> mensagens;
    private EditText mensagemEditText;
    private CollectionReference mMsgsReference;
    private DocumentReference tagMsgsReference;
    private CollectionReference subCOLtagMsgsReference;
    private EditText tagEditText;
    private TextView tagSelecionadaTextView;
    private String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mensagensRecyclerView = findViewById(R.id.mensagensRecyclerView);
        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(mensagens, this);
        mensagensRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mensagensRecyclerView.setLayoutManager(linearLayoutManager);
        mensagemEditText = findViewById(R.id.mensagemEditText);
        tagEditText = findViewById(R.id.tagEditText);
        tagSelecionadaTextView = findViewById(R.id.tagSelecionadaTextView);

    }

    private void setupFirebase (){
        if(tag != "" && tag != null) {
            mMsgsReference = FirebaseFirestore.getInstance().collection("mensagens");
            tagMsgsReference = mMsgsReference.document(tag);
            subCOLtagMsgsReference = tagMsgsReference.collection("envios");
            getRemoteMsgs();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getRemoteMsgs (){
        subCOLtagMsgsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
           @Override
           public void onEvent(
                   @Nullable QuerySnapshot queryDocumentSnapshots,
                   @Nullable FirebaseFirestoreException e)
           {
               mensagens.clear();
               for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                   Mensagem incomingMsg = doc.toObject(Mensagem.class);
                   mensagens.add(incomingMsg);
               }
               Collections.sort(mensagens);
               Collections.reverse(mensagens);
               adapter.notifyDataSetChanged();
           }
       });
    }

    public void buscarTag (View view){
    int lba = tagEditText.getText().toString().length();
    if(lba > 0) {
        tag = tagEditText.getText().toString();
        tagSelecionadaTextView.setText(tag);
        tagEditText.setText("");
        setupFirebase();
    }else{
        Toast.makeText(this, "Digitar uma tag", Toast.LENGTH_SHORT).show();
    }
    }

    public void enviarMensagem (View view){
        if(tag != null && tag != "") {
            String mensagem = mensagemEditText.getText().toString();
            Mensagem m = new Mensagem (new Date(), mensagem);
            esconderTeclado(view);
    //        mMsgsReference.add(m);
    //        tagMsgsReference.set(m);
            subCOLtagMsgsReference.add(m);
            mensagemEditText.setText("");
        }else{
            Toast.makeText(this, "A tag não foi selecionada", Toast.LENGTH_SHORT).show();
        }
    }

    private void esconderTeclado (View v){
        InputMethodManager ims = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        ims.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void exibeFotos(View v){
        Intent intent = new Intent(this, FotosActivity.class);
        startActivity(intent);
    }

    public void exibeTexto(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}

class MensagemViewHolder extends RecyclerView.ViewHolder {
    TextView mensagemTextView;

    MensagemViewHolder(View v) {
        super(v);
        this.mensagemTextView = v.findViewById(R.id.mensagemTextView);
    }
}

class MensagemAdapter extends RecyclerView.Adapter<MensagemViewHolder>{
    private List<Mensagem> mensagens;
    private Context context;
    MensagemAdapter (List<Mensagem> mensagens, Context context){
        this.mensagens = mensagens;
        this.context = context;
    }
    @NonNull
    @Override
    public MensagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.list_item_main, parent,false);
        return new MensagemViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull MensagemViewHolder holder, int position) {
        Mensagem m = mensagens.get(position);
        holder.mensagemTextView.setText(m.getTexto());
    }
    @Override
    public int getItemCount() {
        return mensagens.size();
    }
}