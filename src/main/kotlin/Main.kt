import java.lang.Exception
import java.time.Instant
import java.util.*

fun main(args: Array<String>) {
    var bankAccount = BankAccount("MIKEEURBACCT0001")
    println("Account ${bankAccount.accountName} initialized")
    bankAccount.deposit(120.35)
    bankAccount.query()
    
    try {
        bankAccount.witdraw(300.00)
    }catch (ex: Exception){
        println("Operation not allowed: ${ex.message}")
    }

    try {
        bankAccount.deposit(-300.00)
    }catch (ex: Exception){
        println("Operation not allowed: ${ex.message}")
    }

    bankAccount.query()
    bankAccount.deposit(500.00)
    bankAccount.witdraw(200.00)

    try {
        bankAccount.deposit(0.00)
    }catch (ex: Exception){
        println("Operation not allowed: ${ex.message}")
    }

    bankAccount.query()
    bankAccount.extract()
}

class BankAccount(val accountName: String, var balance: Double = 0.0, var transactions: MutableList<BankTransaction> = ArrayList()) {
    fun deposit(amount: Double){
        if (amount <= 0){
            transactions.add(BankTransaction(UUID.randomUUID().toString(), txnType = TransactionType.DEPOSIT, BankTransactionStatus.FAILED, Instant.now(), amount))
            throw IllegalArgumentException("Operation not allowed: Deposit amount must be strictly positive. Transaction id: ${transactions.last().txnId}")
        }
        balance += amount
        transactions.add(BankTransaction(UUID.randomUUID().toString(), txnType = TransactionType.DEPOSIT, BankTransactionStatus.SUCCESSFUL, Instant.now(), amount))
        println("Deposited $amount EUR, transaction id: ${transactions.last().txnId}")
    }

    fun witdraw(amount: Double){
        if (balance - amount < 0){
            transactions.add(BankTransaction(UUID.randomUUID().toString(), txnType = TransactionType.WITHDRAW, BankTransactionStatus.FAILED, Instant.now(), amount))
            throw IllegalArgumentException("Insufficient funds for withdrawal! Transaction id: ${transactions.last().txnId}")
        }

        balance -= amount
        transactions.add(BankTransaction(UUID.randomUUID().toString(), txnType = TransactionType.WITHDRAW, BankTransactionStatus.SUCCESSFUL, Instant.now(), amount))
        println("$amount EUR withdrawn from the account. Transaction id ${transactions.last().txnId}")
    }

    fun query(){
        transactions.add(BankTransaction(UUID.randomUUID().toString(), txnType = TransactionType.QUERY, BankTransactionStatus.SUCCESSFUL, Instant.now()))
        println("You currently have $balance EUR in your account. Transaction ID: ${transactions.last().txnId}")
    }

    fun extract(){
        transactions.add(BankTransaction(UUID.randomUUID().toString(), txnType = TransactionType.EXTRACT, BankTransactionStatus.SUCCESSFUL, Instant.now()))
        println("The situation of your account with name $accountName now, ${Instant.now()}:")
        println("Balance: $balance EUR")
        println("Transactions:")
        transactions.forEach{transaction ->
            println("""
                Timestamp: ${transaction.timestamp}
                Transaction Id: ${transaction.txnId}
                Type: ${transaction.txnType}
                Status: ${transaction.status}
                Amount: ${transaction.amount}
                """)
        }
    }
}

enum class TransactionType{
    DEPOSIT,
    WITHDRAW,
    QUERY,
    EXTRACT
}

enum class BankTransactionStatus{
    SUCCESSFUL,
    FAILED
}

class BankTransaction(val txnId: String, val txnType: TransactionType, val status: BankTransactionStatus, val timestamp: Instant, val amount: Double = 0.0){
    override fun toString(): String {
        return "BankTransaction(txnId='$txnId', txnType=$txnType, status=$status, timestamp=$timestamp, amount=$amount)"
    }
}