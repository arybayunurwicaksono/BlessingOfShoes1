package com.example.blessingofshoes_1.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.blessingofshoes_1.db.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DbDao {

    @Query("SELECT * FROM users WHERE email LIKE :email")
    fun getUserInfo(email: String) : Users

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun registerUser(users: Users)

    @Query("SELECT * FROM users WHERE email LIKE :email AND password LIKE :password")
    fun readDataUser(email: String, password: String): Users

    @Query("SELECT email FROM users WHERE email=:email")
    fun validateEmail(email: String) : String

    @Query("SELECT COUNT(idUser) FROM users")
    fun validateOwner() : Int

    @Query("SELECT username FROM users WHERE username=:username")
    fun validateUsername(username: String) : String

    @Query("SELECT username FROM users WHERE email LIKE :email")
    fun readUsername(email: String?) : String

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAccounting(accounting: Accounting)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBalance(balance: Balance)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBalanceReport(balanceReport: BalanceReport)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRestock(restock: Restock)

    @Query("SELECT * FROM product")
    fun getAllProduct() : LiveData<List<Product>>

    @Query("SELECT * FROM accounting")
    fun getAllAccounting() : LiveData<List<Accounting>>

    @Query("SELECT * FROM cart")
    fun getAllCartItem() : Flow<List<Cart>>

/*    @Query("SELECT * FROM cart")
    fun readCart() : ArrayList<Cart>*/

    @Query("UPDATE cart SET status = :status WHERE status = 'onProgress'")
    fun updateCartStatus(status: String?)

    @Query("UPDATE balance SET CashBalance = (SELECT SUM(CashBalance + :cashValue) FROM balance WHERE idBalance = 1) WHERE idBalance = 1")
    fun updateCashBalance(cashValue: Int?)

    @Query("UPDATE balance SET digitalBalance = (SELECT SUM(digitalBalance + :digitalValue) FROM balance WHERE idBalance = 1) WHERE idBalance = 1")
    fun updateDigitalBalance(digitalValue: Int?)

    @Query("UPDATE cart SET idTransaction = :idItem WHERE idTransaction = 0")
    fun updateCartIdTransaction(idItem: Int?)

    @Query("SELECT * FROM cart WHERE status LIKE :status")
    fun getCartByStatus(status: String?) : Flow<List<Cart>>
/*    @Query("UPDATE product SET nameProduct =:nameProduct, brandProduct =:brandProduct, priceProduct =:priceProduct, " +
            "stockProduct =:stockProduct, sizeProduct=:sizeProduct, realPriceProduct=:realPriceProduct, " +
            "profitProduct = profitProduct, productPhoto =:productPhoto WHERE idProduct =:idProduct LIKE :idProduct")
    fun updateProduct(idProduct: Int?, nameProduct:String, brandProduct:String, priceProduct:String,
                      stockProduct:Int?, sizeProduct:String, realPriceProduct:String, profitProduct:Int?, productPhoto: Bitmap)*/

    @Query("SELECT * FROM product WHERE idProduct LIKE :idProduct")
    fun readProductItem(idProduct: Int?): LiveData<Product>

    @Query("SELECT * FROM accounting WHERE dateAccounting LIKE :time")
    fun readDetailMonthlyAccounting(time: String?): LiveData<Accounting>

    //UPDATE Products set Price = (SELECT SUM(Price-7) FROM Products WHERE ProductId = 7) WHERE ProductID = 7
    @Query("UPDATE Product set stockProduct = (SELECT SUM(stockProduct- :totalItem) FROM product WHERE idProduct LIKE :idProduct) WHERE idProduct = :idProduct")
    fun sumStockItem(idProduct: Int?, totalItem: Int?)

    @Query("UPDATE Product set stockProduct = (SELECT SUM(stockProduct+ :totalItem) FROM product WHERE idProduct LIKE :idProduct) WHERE idProduct = :idProduct")
    fun sumCancelableStockItem(idProduct: Int?, totalItem: Int?)

    @Query("UPDATE Product set totalPurchases = (SELECT SUM(totalPurchases- :totalPurchases) FROM product WHERE idProduct LIKE :idProduct) WHERE idProduct = :idProduct")
    fun sumTotalPurchasesItem(idProduct: Int?, totalPurchases: Int?)

    @Query("UPDATE Product set totalPurchases = (SELECT SUM(totalPurchases+ :totalPurchases) FROM product WHERE idProduct LIKE :idProduct) WHERE idProduct = :idProduct")
    fun sumCancelableTotalPurchasesItem(idProduct: Int?, totalPurchases: Int?)

    //@Query("SELECT nameProduct FROM product WHERE idProduct LIKE :idProduct LIKE :idProduct")
    //fun readProductName(idProduct: Int): Product

    @Query("SELECT SUM(totalpayment) FROM cart WHERE status = 'onProgress'")
    fun sumTotalPayment(): Int?

    @Query("SELECT SUM(totalTransaction) FROM `transaction`")
    fun sumTotalTransaction(): Int?

    @Query("SELECT SUM(`totalProfit `) FROM cart WHERE status = 'onProgress'")
    fun sumTotalProfit(): Int?

    @Query("SELECT SUM(profitItem) FROM cart WHERE status = 'complete'")
    fun sumTotalCompleteProfit(): Int?

    @Query("SELECT SUM(realPriceProduct*stockProduct) FROM product WHERE idProduct = :idProduct")
    fun sumOldRealPrice(idProduct: Int?): Int?

    /*@Query("SELECT idTransaction FROM `transaction` WHERE idTransaction IN (SELECT MAX(idTransaction) FROM `transaction`)")*/
    @Query("SELECT idTransaction FROM `transaction` ORDER BY idTransaction DESC LIMIT 1")
    fun readLastTransaction(): Int?

    @Query("SELECT idProduct FROM `product` ORDER BY idProduct DESC LIMIT 1")
    fun readLastProduct(): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCart(cart: Cart)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTransaction(transaction: Transaction)

    @Update
    fun updateProductItem(data: Product)

    @Update
    fun updateMonthlyAccounting(data: Accounting)

    @Delete
    fun deleteProductItem(data: Product)

    @Query("DELETE FROM product WHERE idProduct LIKE :idProduct")
    fun deleteProduct(idProduct: Int?)

    @Query("DELETE FROM accounting WHERE idAccounting LIKE :idAccounting")
    fun deleteAccounting(idAccounting: Int?)

    @Query("DELETE FROM cart WHERE idItem LIKE :idItem")
    fun deleteCart(idItem: Int?)

    @Query("SELECT COUNT(idTransaction) FROM `transaction`")
    fun checkTransaction(): Int?

    @Query("SELECT COUNT(idItem) FROM `cart` WHERE status LIKE 'onProgress'")
    fun checkCart(): Int?

    @Query("DELETE FROM `transaction` WHERE idTransaction LIKE :idTransaction")
    fun deleteTransaction(idTransaction: Int?)

/*    @Query("SELECT SUM(idTransaction+1) FROM Cart WHERE idTransaction - (SELECT idTransaction FROM Cart ORDER BY idTransaction DESC LIMIT 1)")
    fun testCart(): Int?*/

    @Query("SELECT status FROM cart WHERE status LIKE :status LIMIT 1")
    fun testCart(status: String?): String?

    @Query("SELECT * FROM `transaction`")
    fun getAllTransaction() : LiveData<List<Transaction>>

    @Query("SELECT * FROM `transaction` WHERE idTransaction LIKE :idTransaction")
    fun readTransactionById(idTransaction: Int?): LiveData<com.example.blessingofshoes_1.db.Transaction>

    @Query("SELECT digitalBalance FROM `balance` WHERE idBalance LIKE 1")
    fun readDigitalBalance(): Int

    @Query("SELECT CashBalance FROM `balance` WHERE idBalance LIKE 1")
    fun readCashBalance(): Int

    @Query("UPDATE balance SET CashBalance = (SELECT SUM(CashBalance - :total) FROM balance WHERE idBalance = 1) WHERE idBalance = 1")
    fun updateCashOutBalance(total: Int?)

    @Query("UPDATE balance SET digitalBalance = (SELECT SUM(digitalBalance - :total) FROM balance WHERE idBalance = 1) WHERE idBalance = 1")
    fun updateDigitalOutBalance(total: Int?)

    @Query("SELECT * FROM cart WHERE idTransaction LIKE :idTransaction AND status LIKE 'complete'")
    fun readTransactionItem(idTransaction: Int?) : Flow<List<Cart>>

    @Query("SELECT * FROM restock")
    fun getRestockData() : Flow<List<Restock>>

    @Query("SELECT * FROM balanceReport")
    fun getBalanceReportData() : Flow<List<BalanceReport>>

    //RECEIPT
    @Query("SELECT COUNT(idTransaction) FROM cart WHERE idTransaction LIKE :idTransaction")
    fun readTotalTransactionRecord(idTransaction: Int?) : Int

    @Query("SELECT nameItem FROM cart WHERE idTransaction LIKE :idTransaction AND status LIKE 'complete' ORDER BY idItem LIMIT :recordPosition,1")
    fun readNameItemReceipt(idTransaction: Int?, recordPosition: Int?) : String

    @Query("SELECT totalItem FROM cart WHERE idTransaction LIKE :idTransaction AND status LIKE 'complete' ORDER BY idItem LIMIT :recordPosition,1")
    fun readTotalItemReceipt(idTransaction: Int?, recordPosition: Int?) : Int

    @Query("SELECT priceItem FROM cart WHERE idTransaction LIKE :idTransaction AND status LIKE 'complete' ORDER BY idItem LIMIT :recordPosition,1")
    fun readPriceItemReceipt(idTransaction: Int?, recordPosition: Int?) : Int

    @Query("SELECT totalpayment FROM cart WHERE idTransaction LIKE :idTransaction AND status LIKE 'complete' ORDER BY idItem LIMIT :recordPosition,1")
    fun readTotalPaymentReceipt(idTransaction: Int?, recordPosition: Int?) : Int

    @Query("SELECT totalTransaction FROM `transaction` WHERE idTransaction LIKE :idTransaction")
    fun readTotalTransactionPayment(idTransaction: Int?) : Int

    @Query("SELECT moneyReceived FROM `transaction` WHERE idTransaction LIKE :idTransaction")
    fun readTotalReceivedPayment(idTransaction: Int?) : Int

    @Query("SELECT moneyChange FROM `transaction` WHERE idTransaction LIKE :idTransaction")
    fun readTotalChangePayment(idTransaction: Int?) : Int

    //accounting
    @Query("SELECT sum(profitTransaction) FROM 'transaction' WHERE transactionDate LIKE :time")
    fun sumTotalProfitAcc(time: String?): Int?

    @Query("SELECT COUNT(idProduct) FROM product")
    fun validateCountProduct(): Int?

    @Query("SELECT COUNT(idBalanceReport) FROM balanceReport")
    fun validateCountBalance(): Int?

    @Query("SELECT COUNT(dateAccounting) FROM accounting WHERE dateAccounting LIKE :datePicker")
    fun validateAccounting(datePicker : String?): Int?

    @Query("SELECT COUNT(idTransaction) FROM `transaction` WHERE transactionDate LIKE :datePicker")
    fun validateTransaction(datePicker : String?): Int?

    @Query("SELECT COUNT(idRestock) FROM `restock` WHERE restockDate LIKE :datePicker")
    fun validateRestock(datePicker : String?): Int?

    @Query("SELECT COUNT(idReturn) FROM `return` WHERE returnDate LIKE :datePicker")
    fun validateReturn(datePicker : String?): Int?

    @Query("SELECT sum(totalPurchases) FROM product")
    fun readTotalStockWorth(): Int?

    @Query("SELECT sum(stockProduct) FROM product")
    fun readTotalStock(): Int?

    @Query("SELECT SUM(totalBalance) FROM balanceReport WHERE reportTag  LIKE 'Capital' AND timeAdded LIKE '%' ||  :timeAdded ||  '%'")
    fun sumTotalInvest(timeAdded: String?): Int?

    @Query("SELECT SUM(totalBalance) FROM balanceReport WHERE status  LIKE 'Out' AND timeAdded LIKE :timeAdded ")
    fun sumTotalBalanceOut(timeAdded: String?): Int?

    @Query("SELECT SUM(totalTransaction) FROM `transaction` WHERE transactionDate LIKE :time ")
    fun sumTotalTransactionAcc(time: String?): Int?

    @Query("SELECT SUM(totalItem) FROM `cart` WHERE status LIKE 'onProgress' ")
    fun sumTotalTransactionItem(): Int?

    @Query("SELECT SUM(totalItem) FROM `transaction` WHERE transactionDate LIKE :time  ")
    fun readTotalTransactionItem(time: String?): Int?

    @Query("SELECT SUM(totalPurchases) FROM `restock` WHERE restockDate LIKE :time ")
    fun sumTotalPurchases(time: String?): Int?

    @Query("SELECT SUM(stockAdded) FROM `restock` WHERE restockDate LIKE :time ")
    fun sumTotalStockAdded(time: String?): Int?

    @Query("SELECT SUM(totalRefund) FROM `return` WHERE returnDate LIKE :time ")
    fun sumTotalRefund(time: String?): Int?

    @Query("SELECT SUM(totalItem) FROM `return` WHERE returnDate LIKE :time ")
    fun sumTotalRefundItem(time: String?): Int?

}