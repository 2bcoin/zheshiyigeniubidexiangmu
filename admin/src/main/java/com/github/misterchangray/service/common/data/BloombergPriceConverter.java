/**
 * MIT License

Copyright (c) 2015  Rob Terpilowski

Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package com.github.misterchangray.service.common.data;



/**
 *
 * @author Rob Terpilowski
 */
public class BloombergPriceConverter {

    
    
    
    public static double nativeToBloomberg( double nativePrice, Commodity commodity ) {
    
            return nativePrice * getMultiplier(commodity);
    }
    
    
    public static double bloombergToNative( double bloombergPrice, Commodity commodity ) {
        return bloombergPrice / getMultiplier(commodity);
    }
    
    
    public static double getMultiplier( Commodity commodity ) {
    
        double multiplier = 0;
        
        
        
        if( commodity == Commodity.JAPANESE_YEN_GLOBEX ) {
                multiplier = 10000.0;
        } else if( commodity == Commodity.CANADIAN_DOLLAR_GLOBEX ||
                commodity == Commodity.COPPER_NYMEX ||
                commodity == Commodity.HEATING_OIL_NYMEX ||
                commodity == Commodity.SWISS_FRANC_GLOBEX ) {
            multiplier  = 100.0;
        } else if( commodity == Commodity.CORN_ECBOT ||
                commodity == Commodity.CRUDE_OIL_NYMEX ||
                commodity == Commodity.DOW_INDEX_MINI_ECBOT ||
                commodity == Commodity.EURO_GLOBEX ||
                commodity == Commodity.GOLD_NYMEX ||
                commodity == Commodity.NASDAQ100_INDEX_MINI_GLOBEX ||
                commodity == Commodity.NATURAL_GAS_NYMEX ||
                commodity == Commodity.SILVER_NYMEX ||
                commodity == Commodity.SOYBEANS_ECBOT ||
                commodity == Commodity.SP500_INDEX_MINI_GLOBEX ||
                commodity == Commodity.BOND_30_YEAR_ECBOT ||
                commodity == Commodity.BOND_10_YEAR_ECBOT ||
                commodity == Commodity.WHEAT_ECBOT ) {
            multiplier = 1.0;
        } else {
            throw new IllegalStateException( "Unknown Commodity: " + commodity );
        }
        
        return multiplier;
    }        
    
}
