package com.parkngo.parkngo.interfaces;

import com.parkngo.parkngo.data.rates.Rate;

public interface UpdatePrice {
    void onPriceUpdated(Rate rate, double updated, boolean delete);
}
