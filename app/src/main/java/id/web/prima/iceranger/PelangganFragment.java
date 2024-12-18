package id.web.prima.iceranger;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PelangganFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PelangganFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PelangganFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PelangganFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PelangganFragment newInstance(String param1, String param2) {
        PelangganFragment fragment = new PelangganFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    FirebaseFirestore db; RecyclerView pelanggan; ArrayList<ModelPelanggan> arrayList;
    PelangganAdapterRecycler adapter; ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pelanggan, container, false);
        db = FirebaseFirestore.getInstance();
        pelanggan = view.findViewById(R.id.recycler_pelanggan);
        arrayList = new ArrayList<ModelPelanggan>();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang mengambil data...");
        progressDialog.show();

        pelanggan.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PelangganAdapterRecycler(getActivity(),arrayList);
        pelanggan.setAdapter(adapter);
        TampilkanDataPelanggan();
        return view;
    }
    private void TampilkanDataPelanggan() {
        db.collection("pelanggan").orderBy("created_at", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                if (list.size() < 1 || list.isEmpty()){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Data Pelanggan masih kosong!",
                            Toast.LENGTH_LONG).show();
                }else{
                    for (DocumentSnapshot d:list){
                        ModelPelanggan obj = d.toObject(ModelPelanggan.class);
                        obj.setId_Pelanggan(d.getId());
                        arrayList.add(obj);
                    }
                    adapter.notifyDataSetChanged();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(getActivity(), "Data Pelanggan masih kosong!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}