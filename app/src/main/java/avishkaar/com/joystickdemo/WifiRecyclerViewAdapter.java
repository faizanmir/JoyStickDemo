package avishkaar.com.joystickdemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class WifiRecyclerViewAdapter extends RecyclerView.Adapter<WifiRecyclerViewAdapter.WifiNetworkViewHolder> {
    ArrayList<String> wifiScanResult;
    interface SSIDPass{
        void passSSID(String SSID);
    }
    SSIDPass ref;


    public WifiRecyclerViewAdapter(ArrayList<String> wifiScanResult, SSIDPass ref) {
        this.wifiScanResult = wifiScanResult;
        this.ref = ref;
    }



    @NonNull
    @Override
    public WifiNetworkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new WifiNetworkViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_layout,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final WifiNetworkViewHolder wifiNetworkViewHolder, int i) {
        wifiNetworkViewHolder.networkName.setText(wifiScanResult.get(i));
        wifiNetworkViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.passSSID(wifiScanResult.get(wifiNetworkViewHolder.getAdapterPosition()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return wifiScanResult.size();
    }

    class WifiNetworkViewHolder extends RecyclerView.ViewHolder{
        TextView networkName;
        TextView networkAddress;


        public WifiNetworkViewHolder(@NonNull View itemView) {
            super(itemView);
            networkAddress = itemView.findViewById(R.id.networkName);
            networkName = itemView.findViewById(R.id.ssid);
        }
    }
}
