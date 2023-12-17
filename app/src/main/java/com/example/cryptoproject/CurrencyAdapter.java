package com.example.cryptoproject;
// CurrencyAdapter.java

// Import statements

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;
import com.squareup.picasso.Picasso;

public class CurrencyAdapter extends ArrayAdapter<Currency> {

    private Context context;
    private int resource;

    public CurrencyAdapter(Context context, int resource, List<Currency> currencies) {
        super(context, resource, currencies);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Implement the view holder pattern for better performance
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.currencyIcon = convertView.findViewById(R.id.currencyIcon);
            viewHolder.currencyFullName = convertView.findViewById(R.id.currencyFullName);
            viewHolder.exchangeRate = convertView.findViewById(R.id.exchangeRate);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Currency currency = getItem(position);

        if (currency != null) {
            Picasso.get().load(currency.getIconUrl()).into(viewHolder.currencyIcon);
            // Set data to views
            // You can use a library like Picasso or Glide for image loading
            // viewHolder.currencyIcon.setImageResource(R.drawable.ic_launcher_foreground);
            // Picasso.get().load(currency.getIconUrl()).into(viewHolder.currencyIcon);
            viewHolder.currencyFullName.setText(currency.getFullName());
            viewHolder.exchangeRate.setText(String.format(Locale.getDefault(), "%.6f", currency.getExchangeRate()));
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView currencyIcon;
        TextView currencyFullName;
        TextView exchangeRate;
    }
}

