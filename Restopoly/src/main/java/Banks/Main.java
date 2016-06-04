package Banks;

import Banks.BankManagerComponent.BankManager;
import Common.Abstract.MainAbstract;
import Common.Exceptions.*;
import Common.Util.DebugService;
import Common.Util.IPFinder;
import YellowPage.RegistrationService;
import YellowPage.YellowPageService;
import javafx.util.Pair;
import spark.Request;
import spark.Response;
import static spark.Spark.*;

/**
 * Created by dima on 27.05.16.
 */
public class Main extends MainAbstract{

    public static int    port = 4567;
    public static String ip = IPFinder.getIP();

    public static String name = "group_42";
    public static String description = "Banks Manager";
    public static String service = "banks";

    public static String URL = "http://"+ip+":"+port;
    public static String URLService = URL+"/banks";

    public Main(){
        super(port,ip,name,description,service,URLService);
    }

    public static void main(String[] args) {

        System.out.println("=== Banks ===");
        port(port);
        Main main = new Main();
        BankManager bankManager = new BankManager();

        RegistrationService registrationService = new RegistrationService(main);
        registrationService.startRegistration();

        YellowPageService.startListening();
        DebugService.start();

        /**
         * List of available banks
         */
        get("/banks", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String banksJsonArrayString = bankManager.getBanks();
            return banksJsonArrayString;
        });

        /**
         * creates a new bank
         */
        post("/banks", (req, res) -> {
            res.status(201);
            checkContentTypeJson(req);
            setResponseContentTypeJson(res);

            String jsonBody = req.body();
            String bankID = bankManager.createBank(jsonBody);

            setLocationHeader(res, URL + bankID);
            return bankManager.getBankById(bankID);
        });

        /**
         * Gets a bank
         */
        get("/banks/:bankID", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String bankID = "/banks/" + req.params(":bankID");

            String bankJsonString = bankManager.getBankById(bankID);
            return bankJsonString;
        });

        /**
         * places a banks
         */
        put("/banks/:bankID", (req, res) -> {
            res.status(200);
            checkContentTypeJson(req);
            setResponseContentTypeJson(res);

            String oldBankID = "/banks/" + req.params(":bankID");
            String jsonBody  = req.body();
            String bankID = bankManager.updateBankById(oldBankID,jsonBody);
            setLocationHeader(res, URL + bankID);
            return bankManager.getBankById(bankID);
        });

        /**
         * List of available transfer
         */
        get("/banks/:bankID/transfers", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String bankID = "/banks/" + req.params(":bankID");
            String transfersJsonArrayString = bankManager.getBankTransfersListById(bankID);
            return transfersJsonArrayString;
        });

        /**
         * Gets a transfer
         */
        get("/banks/:bankID/transfers/:transferID", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String bankID = "/banks/" + req.params(":bankID");
            String transferID = bankID + "/transfers/" + req.params(":transferID");

            String banksTransferJsonString = bankManager.getBankTransfer(bankID, transferID);
            return banksTransferJsonString;
        });

        /**
         * creates a new bank transfer from a account id to an other
         */
        post("/banks/:bankID/transfer/from/:fromID/to/:toID/:amount", (req, res) -> {
            res.status(201);
            checkContentTypeJson(req);
            setResponseContentTypeJson(res);

            String bankID = "/banks/" + req.params(":bankID");
            String fromAccount = bankID + "/accounts/" + req.params(":fromID");
            String toAccount = bankID + "/accounts/" + req.params(":toID");
            int amount = Integer.parseInt(req.params(":amount"));

            String reason = req.body();

            // eventuell den Querry parameter beachten
            String transactionID = req.queryParams("transaction");

            if(transactionID == null){
                Pair<String,String> transactionPair = bankManager.createTransferFromTo(bankID, fromAccount, toAccount, amount, reason);
                setLocationHeader(res,URL+transactionPair.getKey());
                return transactionPair.getValue();
            }else{
                transactionID = bankID+"/transaction/"+transactionID;
                Pair<String,String> transactionPair = bankManager.createTransferFromTo(bankID,fromAccount,toAccount,amount,reason,transactionID);
                setLocationHeader(res,URL+transactionPair.getKey());
                return transactionPair.getValue();
            }
        });

        /**
         * creates a new bank transfer from the bank itself
         */
        post("/banks/:bankID/transfer/to/:toID/:amount", (req, res) -> {
            res.status(201);
            checkContentTypeJson(req);
            setResponseContentTypeJson(res);

            String bankID = "/banks/" + req.params(":bankID");
            String toAccount = bankID + "/accounts/" + req.params(":toID");
            int amount = Integer.parseInt(req.params(":amount"));

            String reason = req.body();

            // eventuell den Querry parameter beachten
            String transactionID = req.queryParams("transaction");

            if(transactionID == null){
                Pair<String,String> transactionPair = bankManager.createTransferTo(bankID, toAccount, amount, reason);
                setLocationHeader(res,URL+transactionPair.getKey());
                return transactionPair.getValue();
            }else{
                transactionID = bankID+"/transaction/"+transactionID;
                Pair<String,String> transactionPair = bankManager.createTransferTo(bankID, toAccount, amount, reason,transactionID);
                setLocationHeader(res,URL+transactionPair.getKey());
                return transactionPair.getValue();
            }
        });

        /**
         * creates a new bank transfer to the bank itself
         */
        post("/banks/:bankID/transfer/from/:fromID/:amount", (req, res) -> {
            res.status(201);
            checkContentTypeJson(req);
            setResponseContentTypeJson(res);

            String bankID = "/banks/" + req.params(":bankID");
            String fromAccount = bankID + "/accounts/" + req.params(":fromID");
            int amount = Integer.parseInt(req.params(":amount"));

            String reason = req.body();

            // eventuell den Querry parameter beachten
            String transactionID = req.queryParams("transaction");

            if(transactionID == null){
                Pair<String,String> transactionPair = bankManager.createTransferFrom(bankID, fromAccount, amount, reason);
                setLocationHeader(res,URL+transactionPair.getKey());
                return transactionPair.getValue();
            }else{
                transactionID = bankID+"/transaction/"+transactionID;
                Pair<String,String> transactionPair = bankManager.createTransferFrom(bankID, fromAccount, amount, reason,transactionID);
                setLocationHeader(res,URL+transactionPair.getKey());
                return transactionPair.getValue();
            }
        });

        /**
         * begins a new transaction
         */
        post("/banks/:bankID/transaction", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String bankID = "/banks/" + req.params(":bankID");
            String transactionID = bankManager.createTransactionId(bankID);

            setLocationHeader(res, URL + transactionID);
            return bankManager.getTransactionById(bankID, transactionID);
        });

        /**
         * returns the state of the transaction
         */
        get("/banks/:bankID/transaction/:tid", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String bankID = "/banks/" + req.params(":bankID");
            String transactionID = bankID + "/transaction/" + req.params(":tid");

            String transactionJsonString = bankManager.getTransactionById(bankID, transactionID);
            return transactionJsonString;
        });

        /**
         * commits/readies the transaction
         */
        put("/banks/:bankID/transaction/:tid", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String bankID = "/banks/" + req.params(":bankID");
            String transactionID = bankID + "/transaction/" + req.params(":tid");

            // get Querryparameter
            // TODO wie sollen diese aussehen ????
            String state = req.queryParams("state");
            if(state == null) throw new QuerryParamsNotFoundException("Querry param \"state\" not found");
            state = state.toUpperCase();

            String transactionJsonString = bankManager.updateTransactionById(bankID, transactionID, state);
            return transactionJsonString;
        });

        /**
         * abort/rollback an transaction
         */
        delete("/banks/:bankID/transaction/:tid", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String bankID = "/banks/" + req.params(":bankID");
            String transactionID = bankID + "/transaction/" + req.params(":tid");

            String transactionJsonString = bankManager.deleteTransactionById(bankID, transactionID);
            return transactionJsonString;
        });

        /**
         * List of available account
         */
        get("/banks/:bankID/accounts", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String bankID = "/banks/" + req.params(":bankID");
            String accountJsonArrayString = bankManager.getBankAccountListById(bankID);

            return accountJsonArrayString;
        });

        /**
         * creates a bank account
         */
        post("/banks/:bankID/accounts", (req, res) -> {
            res.status(201);
            checkContentTypeJson(req);
            setResponseContentTypeJson(res);

            String bankID = "/banks/" + req.params(":bankID");
            String jsonBody = req.body();

            String accountID = bankManager.createBankAccount(bankID, jsonBody);

            setLocationHeader(res, URL + accountID);
            return bankManager.getBankAccountById(bankID, accountID);
        });

        /**
         * returns account the saldo of the player
         */
        get("/banks/:bankID/accounts/:accountID", (req, res) -> {
            res.status(200);
            setResponseContentTypeJson(res);

            String bankID = "/banks/" + req.params(":bankID");
            String accountID = bankID + "/accounts/" + req.params(":accountID");

            String accountJsonString = bankManager.getBankAccountById(bankID, accountID);
            return accountJsonString;
        });


        /****** EXCEPTIONS ******/

        exception(UnsupportedOperationException.class, (ex,req,res) -> {
            res.status(500); // Not implemented
            res.body("Method not implemented");
            ex.printStackTrace();
        });

        exception(WrongContentTypeException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body("Wrong Content-Type");
            ex.printStackTrace();
        });

        exception(BankNotFoundException.class, (ex, req, res) -> {
            res.status(404);// not found
            res.body("Bank Not Found");
            ex.printStackTrace();
        });

        exception(BankAlreadyExistsException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body("Bank Already Exists");
            ex.printStackTrace();
        });

        exception(TransferNotFoundException.class, (ex, req, res) -> {
            res.status(404);// not found
            res.body("Transfer not found");
            ex.printStackTrace();
        });

        exception(IllegalTransactionStateException.class, (ex, req, res) -> {
            res.status(403);// forbidden
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

        exception(TransferFailedException.class, (ex, req, res) -> {
            res.status(403);// forbidden
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

        exception(UndefinedTransactionStateException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

        exception(AccountNotFoundException.class, (ex, req, res) -> {
            res.status(404);// not found
            res.body("Account not found");
            ex.printStackTrace();
        });

        exception(ServiceNotAvaibleException.class, (ex, req, res) -> {
            res.status(500);// not found
            res.body(ex.getMessage());
            ex.printStackTrace();
        });

        exception(QuerryParamsNotFoundException.class, (ex, req, res) -> {
            res.status(400);// bad request
            res.body(""+ex.getMessage());
            ex.printStackTrace();
        });

        exception(TransactionFailedException.class, (ex, req, res) -> {
            res.status(403);// bad request
            res.body(""+ex.getMessage());
            ex.printStackTrace();
        });

    }

    private static void checkContentTypeJson(Request req) throws WrongContentTypeException {
        if (!req.headers("Content-Type").equals("application/json")) throw new WrongContentTypeException();
    }

    private static void setResponseContentTypeJson(Response res) {
        res.header("Content-Type", "application/json");
    }

    private static void setLocationHeader(Response res, String URL) {
        res.header("Location", URL);
    }
}
