package com.espressoshock.drinkle.models;

public class Account {
    private String accountID;
    private String accountName;
    private String accountPassword;
    private Person registered;
    private Blueprint blueprint;

    public Account(String accountID, String accountName,String accountPassword,Person registered,
    Blueprint blueprint){
    this.accountID = accountID;
    this.accountName = accountName;
    this.accountPassword = accountPassword;
    this.registered = registered;
    this.blueprint = blueprint;
    }

    public String getAccountID(){return accountID;}

    public String getAccountName(){return accountName;}

    public String getAccountPassword(){return accountPassword;}

    public Person getRegistered(){return registered;}

    public Blueprint getBlueprint(){return blueprint;}

    public void setAccountID(String accountID){this.accountID = accountID;}

    public void setAccountName(String accountName){this.accountName = accountName;}

    public void setAccountPassword(String accountPassword){this.accountPassword = accountPassword;}

    public void setRegistered(Person registered){this.registered = registered;}

    public void setBlueprint(Blueprint blueprint){this.blueprint = blueprint;}

    @Override
    public String toString() {
        return "Account{" +
                "accountID='" + accountID + '\'' +
                ", accountName='" + accountName + '\'' +
                ", accountPassword='" + accountPassword + '\'' +
                ", registered=" + registered +
                ", blueprint=" + blueprint +
                '}';
    }
}
