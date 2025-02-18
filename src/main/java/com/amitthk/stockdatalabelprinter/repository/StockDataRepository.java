package com.amitthk.stockdatalabelprinter.repository;

import com.amitthk.stockdatalabelprinter.entity.StockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockDataRepository extends JpaRepository<StockData, Long> {
    StockData findBySymbol(String symbol);
}
