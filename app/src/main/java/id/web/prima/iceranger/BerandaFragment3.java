package id.web.prima.iceranger;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BerandaFragment3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BerandaFragment3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BerandaFragment3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BerandaFragment3.
     */
    // TODO: Rename and change types and number of parameters
    public static BerandaFragment3 newInstance(String param1, String param2) {
        BerandaFragment3 fragment = new BerandaFragment3();
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
    TextView nama, jabatan;
    CardView pesanan,addpesanan,addpelanggan,pengriman,pengguna,addpengguna;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beranda3, container, false);
        nama = view.findViewById(R.id.tv_namaDash);
        jabatan = view.findViewById(R.id.tv_jabatanDash);
        pesanan = view.findViewById(R.id.sec_pesanan);
        addpesanan = view.findViewById(R.id.sec_newpesanan);
        addpelanggan = view.findViewById(R.id.sec_newpelanggan);
        pengriman = view.findViewById(R.id.sec_pengiriman);
        pengguna = view.findViewById(R.id.sec_pengguna);
        addpengguna = view.findViewById(R.id.sec_newpengguna);
        sesiLogin user = new sesiLogin();
        nama.setText(user.getNama().toString().toUpperCase());
        jabatan.setText(user.getJabatan().toString());
        pesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                startActivity(new Intent(getActivity(), PesananActivity.class));
            }
        });
        addpesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                startActivity(new Intent(getActivity(), TambahPesanan.class));
            }
        });
        addpelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                startActivity(new Intent(getActivity(), TambahPelanggan.class));
            }
        });
        pengriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                startActivity(new Intent(getActivity(), PengirimanActivity.class));
            }
        });
        pengguna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                startActivity(new Intent(getActivity(), PenggunaActivity.class));
            }
        });
        addpengguna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                startActivity(new Intent(getActivity(), TambahPengguna.class));
            }
        });
        return view;
    }
}