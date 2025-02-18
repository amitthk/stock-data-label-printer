package com.amitthk.stockdatalabelprinter.service;

import com.amitthk.stockdatalabelprinter.entity.StockData;
import com.amitthk.stockdatalabelprinter.repository.StockDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class StockAnalysisService {

    private final StockDataRepository stockDataRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public StockAnalysisService(StockDataRepository stockDataRepository) {
        this.stockDataRepository = stockDataRepository;
    }

    public CompletableFuture<StockData> fetchFundamentalData(String symbol) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Fetching data for {}", symbol);
                Stock yahooStock = YahooFinance.get(symbol);

// Assuming yahooStock is an instance of YahooFinance Stock object
                StockData stock = new StockData();
                stock.setSymbol(symbol);
                stock.setCompanyName(yahooStock.getName());
                stock.setPeRatio(yahooStock.getStats().getPe());
                stock.setPbRatio(yahooStock.getStats().getPriceBook());
                stock.setMarketCap(BigDecimal.valueOf(yahooStock.getStats().getMarketCap().doubleValue()));
                stock.setDividendYield(yahooStock.getDividend().getAnnualYieldPercent());
                //stock.setRoe(yahooStock.getStats().getReturnOnEquity());  // Fixed: Changed from getRevenue to getReturnOnEquity
                stock.setEps(yahooStock.getStats().getEps());
//                stock.setOperatingMargin(yahooStock.getStats().getOperatingMargins());  // Fixed: Changed to getOperatingMargins
//                stock.setFreeCashFlow(yahooStock.getStats().getOperatingCashFlow());    // Note: Using operating cash flow as direct FCF isn't available
//                stock.setDebtToEquity(yahooStock.getStats().getDebtToEquity());        // Fixed: Changed to getDebtToEquity
//                stock.setRevenueGrowth(yahooStock.getStats().getQuarterlyRevenueGrowth()); // Fixed: Using quarterly revenue growth
//                stock.setEarningsGrowth(yahooStock.getStats().getQuarterlyEarningsGrowth()); // Fixed: Using quarterly earnings growth
//                stock.setBeta(yahooStock.getStats().getBeta());
                stockDataRepository.save(stock);
                return stock;

            } catch (IOException e) {
                throw new RuntimeException("Error fetching stock data", e);
            }
        }, executorService);
    }

    public List<StockData> getAllStocks() {
        return stockDataRepository.findAll();
    }

    public StockData getStockBySymbol(String symbol) {
        return stockDataRepository.findBySymbol(symbol);
    }
}
