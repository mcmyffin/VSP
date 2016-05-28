package Banks.BankManagerComponent;

import Banks.BankManagerComponent.DTOs.AccountDTO;
import Banks.BankManagerComponent.DTOs.BankDTO;
import Banks.BankManagerComponent.DTOs.TransferDTO;
import Banks.Main;
import Common.Exceptions.*;
import Common.Util.URIObject;
import Common.Util.URIParser;
import Events.EventManagerComponent.DTO.EventDTO;
import Games.GameManagerComponent.DTO.ComponentsDTO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.util.Pair;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dima on 27.05.16.
 */
public class BankManager {

    private final Gson gson;
    private final Map<String,Bank> bankMap;

    public BankManager() {
        this.gson = new Gson();
        this.bankMap = new HashMap();
    }


    private Bank getBankObjectById(String bankID) throws BankNotFoundException {
        checkNotNull(bankID);

        if(!bankMap.containsKey(bankID)) throw new BankNotFoundException();
        return bankMap.get(bankID);
    }

    private EventDTO createEvent(Bank bank, String type, String name, String reason, String resource) throws ServiceNotAvaibleException, WrongFormatException {
        String game = bank.getGameURIObject().getAbsoluteURI();
        String time = Long.toString(System.currentTimeMillis());
        String player = "";

        EventDTO eventPostDTO = new EventDTO(
                game,
                type,
                name,
                reason,
                resource,
                player,
                time
        );

        try{
            HttpResponse<String> componentsResponse = Unirest.get(game+"/components").asString();
            if(!componentsResponse.getHeaders().get("Content-Type").contains("application/json")) throw new ServiceNotAvaibleException("Games Service response wrong");

            String eventManagerURI = gson.fromJson(componentsResponse.getBody(),ComponentsDTO.class).getEvent();

            HttpResponse<String> eventPostResponse = Unirest.post(eventManagerURI).header("Content-Type","application/json").body(eventPostDTO).asString();
            if(eventPostResponse.getStatus() != 201 || eventPostResponse.getStatus() != 200) throw new ServiceNotAvaibleException("Event Service POST -  Wrong response code");

            HttpResponse<String> eventGetResponse = Unirest.get(eventPostResponse.getHeaders().getFirst("Location")).asString();
            if(eventGetResponse.getStatus() != 200) throw new ServiceNotAvaibleException("Event Service GET - Wrong response code");

            EventDTO eventGetDTO = gson.fromJson(eventGetResponse.getBody(),EventDTO.class);
            return eventGetDTO;

        }catch (UnirestException ex){
            throw new ServiceNotAvaibleException(ex.getMessage());
        }catch (JsonSyntaxException ex){
            throw new WrongFormatException();
        }
    }

    /***************************/

    public String getBanks() {
        Collection<String> banksCollection = bankMap.keySet();
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("banks", gson.toJson(banksCollection));
        return jsonObject.toString();
    }

    public String createBank(String jsonBody) throws WrongFormatException, URISyntaxException, BankAlreadyExistsException {
        checkNotNull(jsonBody);

        JSONObject jsonObject = new JSONObject(jsonBody);
        if(!jsonObject.has("game")) throw new WrongFormatException();

        // get game URI from Json Body
        String gameURI = jsonObject.getString("game");
        URIObject uriObject = URIParser.createURIObject(gameURI);

        // create Bank Object
        Bank bank = new Bank(uriObject);

        // if bankID already exists -> Exception
        if(bankMap.containsKey(bank.getId())) throw new BankAlreadyExistsException();

        bankMap.put(bank.getId(),bank);
        return bank.getId();
    }

    public String getBankById(String bankID) throws BankNotFoundException {
        checkNotNull(bankID);

        Bank bank = getBankObjectById(bankID);
        BankDTO bankDTO = bank.toDTO();

        return gson.toJson(bankDTO);
    }

    public String updateBankById(String oldBankID, String jsonBody) throws BankNotFoundException, WrongFormatException, URISyntaxException, BankAlreadyExistsException {
        checkNotNull(oldBankID);
        checkNotNull(jsonBody);

        // get existing Bank
        Bank bank               = getBankObjectById(oldBankID);

        // parse jsonBody to Json Object
        JSONObject jsonObject   = new JSONObject(jsonBody);

        // jsonObject not contains key -> Exception
        if(!jsonObject.has("game")) throw new WrongFormatException();

        // get GameURI from jsonObject
        String gameURI      = jsonObject.getString("game");
        URIObject uriObject = URIParser.createURIObject(gameURI);

        // update GameURI
        Bank updatedBank = bank.updateBankGameID(uriObject);

        // if new GameURI already linked with Bank -> Exception
        if(bankMap.containsKey(updatedBank.getId())) throw new BankAlreadyExistsException();

        // delete old Bank from Map
        bankMap.remove(bank.getId());

        // insert updated Bank to Map
        bankMap.put(updatedBank.getId(),updatedBank);

        return updatedBank.getId();
    }

    public String getBankTransfersListById(String bankID) throws BankNotFoundException {
        checkNotNull(bankID);

        Bank bank = getBankObjectById(bankID);
        Collection<String> transfersList = bank.getTransfersList();

        return gson.toJson(transfersList);
    }

    public String getBankTransfer(String bankID, String transferID) throws BankNotFoundException, TransferNotFoundException {
        checkNotNull(bankID);
        checkNotNull(transferID);

        Bank bank = getBankObjectById(bankID);
        Transfer transfer       = bank.getTransfer(transferID);
        TransferDTO transferDTO = transfer.toDTO();

        return gson.toJson(transferDTO);
    }

    public String createTransferFromTo(String bankID, String fromAccount, String toAccount, int amount, String reason, String transactionID) {
        throw new UnsupportedOperationException();
    }

    public Pair<String,String> createTransferFromTo(String bankID, String fromAccount, String toAccount, int amount, String reason)
            throws BankNotFoundException, TransferFailedException, AccountNotFoundException, ServiceNotAvaibleException, WrongFormatException {
        checkNotNull(bankID);
        checkNotNull(fromAccount);
        checkNotNull(toAccount);
        checkNotNull(reason);

        Bank bank = getBankObjectById(bankID);
        List<EventDTO> eventList = new ArrayList();

        String transferID = bank.createTransferFromTo(fromAccount,toAccount,amount,reason);

        // create Event
        EventDTO event = createEvent(
                                    bank,
                                    "bank transfer from/to Account",
                                    "Bank Transfer",
                                    reason,
                                    Main.URL+transferID
                            );

        // add Event to List
        eventList.add(event);

        // convert to JsonString
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("events",gson.toJson(eventList));

        // create Pair<transferID,EventJsonArrayString>
        return new Pair<String,String>(transferID,jsonObject.toString());
    }

    public String createTransferTo(String bankID, String toAccount, int amount, String reason, String transactionID) {
        throw new UnsupportedOperationException();
    }

    public Pair<String,String> createTransferTo(String bankID, String toAccount, int amount, String reason)
            throws BankNotFoundException, ServiceNotAvaibleException, WrongFormatException, TransferFailedException, AccountNotFoundException {
        checkNotNull(bankID);
        checkNotNull(toAccount);
        checkNotNull(reason);

        Bank bank = getBankObjectById(bankID);
        List<EventDTO> eventList = new ArrayList();

        String transferID = bank.createTransferTo(toAccount,amount,reason);

        // create Event
        EventDTO event = createEvent(
                bank,
                "bank transfer to Account",
                "Bank Transfer",
                reason,
                Main.URL+transferID
        );

        // add Event to List
        eventList.add(event);

        // convert to JsonString
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("events",gson.toJson(eventList));

        // create Pair<transferID,EventJsonArrayString>
        return new Pair<String,String>(transferID,jsonObject.toString());
    }

    public String createTransferFrom(String bankID, String fromAccount, int amount, String reason, String transactionID) {
        throw new UnsupportedOperationException();
    }

    public Pair<String,String> createTransferFrom(String bankID, String fromAccount, int amount, String reason) throws BankNotFoundException, ServiceNotAvaibleException, WrongFormatException, TransferFailedException, AccountNotFoundException {
        checkNotNull(bankID);
        checkNotNull(fromAccount);
        checkNotNull(reason);

        Bank bank = getBankObjectById(bankID);
        List<EventDTO> eventList = new ArrayList();

        String transferID = bank.createTransferFrom(fromAccount,amount,reason);

        // create Event
        EventDTO event = createEvent(
                bank,
                "bank transfer from Account",
                "Bank Transfer",
                reason,
                Main.URL+transferID
        );

        // add Event to List
        eventList.add(event);

        // convert to JsonString
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("events",gson.toJson(eventList));

        // create Pair<transferID,EventJsonArrayString>
        return new Pair<String,String>(transferID,jsonObject.toString());
    }

    public String createTransactionId(String bankID) throws BankNotFoundException {
        checkNotNull(bankID);

        Bank bank = getBankObjectById(bankID);

        String transactionID = bank.createTransactionId();
        return transactionID;
    }

    public String getTransactionById(String bankID, String transactionID) throws BankNotFoundException, TransactionNotFoundException {
        checkNotNull(bankID);
        checkNotNull(transactionID);

        Bank bank = getBankObjectById(bankID);

        Transaction t = bank.getTransactionById(transactionID);
        return t.getState().getVal();
    }

    public String updateTransactionById(String bankID, String transactionID, String state)
                    throws BankNotFoundException, TransactionNotFoundException, UndefinedTransactionStateException,
                    IllegalTransactionStateException {

        checkNotNull(bankID);
        checkNotNull(transactionID);
        checkNotNull(state);

        Bank bank = getBankObjectById(bankID);
        Transaction transaction = bank.getTransactionById(transactionID);

        transaction.setTransferAction(state);
        return transactionID;
    }

    public String deleteTransactionById(String bankID, String transactionID) throws BankNotFoundException, TransactionNotFoundException, UndefinedTransactionStateException, IllegalTransactionStateException {
        checkNotNull(bankID);
        checkNotNull(transactionID);

        Bank bank = getBankObjectById(bankID);
        Transaction transaction = bank.getTransactionById(transactionID);

        String state = TransferAction.ROLLBACK.getVal();
        transaction.setTransferAction(state);

        return transactionID;
    }

    public String getBankAccountListById(String bankID) throws BankNotFoundException {
        checkNotNull(bankID);

        Bank bank = getBankObjectById(bankID);
        Collection<String> accountList = bank.getAccountList();

        return gson.toJson(accountList);
    }

    public String createBankAccount(String bankID, String jsonBody) throws BankNotFoundException, WrongFormatException, RequiredJsonParamsNotFoundException {
        checkNotNull(bankID);
        checkNotNull(jsonBody);

        try{
            Bank bank = getBankObjectById(bankID);

            // parse Accountinformation from JsonBody
            AccountDTO accountDTO = gson.fromJson(jsonBody,AccountDTO.class);
            accountDTO.checkContructorArguments();

            String accountID = bank.createAccount(accountDTO);
            return accountID;
        }catch (JsonSyntaxException ex){
            throw new WrongFormatException("Wrong json syntax");
        }
    }

    public String getBankAccountById(String bankID, String accountID) throws BankNotFoundException, AccountNotFoundException {
        checkNotNull(bankID);
        checkNotNull(accountID);

        Bank bank = getBankObjectById(bankID);
        Account account = bank.getAccountById(accountID);

        AccountDTO accountDTO = account.toDTO();
        return gson.toJson(accountDTO);
    }
}
