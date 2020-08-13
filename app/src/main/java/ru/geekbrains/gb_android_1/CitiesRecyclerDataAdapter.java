package ru.geekbrains.gb_android_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CitiesRecyclerDataAdapter extends RecyclerView.Adapter<CitiesRecyclerDataAdapter.ViewHolder> {
    private List<String> cities;
    private RVOnItemClick onItemClickCallback;

    public CitiesRecyclerDataAdapter(List<String> cities, RVOnItemClick onItemClickCallback) {
        this.cities = cities;
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cities_recyclerview_layout, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = cities.get(position);

        holder.setTextToTextView(text);
        holder.setOnClickForItem(text);
    }

    @Override
    public int getItemCount() {
        return cities == null ? 0 : cities.size();
    }


    public void putChosenCityToTopInCitiesList(String chosenCity){
        Collections.swap(cities, 0 , cities.indexOf(chosenCity));
    }

    public void addNewCity(String newElement) {
        cities.set(0, newElement);
        notifyItemInserted(0);
    }
    public List<String> getCitiesList(){return cities;}

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cityName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.cityNameTextView);
        }

        void setTextToTextView(String text) {
            cityName.setText(text);
        }

        void setOnClickForItem(final String text) {
            cityName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickCallback != null) {
                        onItemClickCallback.onItemClicked(text);
                    }
                }
            });
        }
    }
}
