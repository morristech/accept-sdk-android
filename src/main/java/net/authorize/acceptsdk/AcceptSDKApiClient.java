package net.authorize.acceptsdk;

import android.content.Context;
import java.lang.ref.WeakReference;
import net.authorize.acceptsdk.datamodel.transaction.EncryptTransactionObject;
import net.authorize.acceptsdk.datamodel.transaction.callbacks.EncryptTransactionCallback;
import net.authorize.acceptsdk.exception.AcceptSDKException;
import net.authorize.acceptsdk.internal.AcceptSDKCore;
import net.authorize.acceptsdk.network.ConnectionData;

/**
 * API client class.
 *
 * Created by kbollepa on 7/7/2016.
 */
public class AcceptSDKApiClient {

  //COMMENT: context is needed to start service.
  private static WeakReference<Context> sContext;

  public enum Environment {SANDBOX, PRODUCTION}

  ;

  private final Environment mEnvironment;

  public AcceptSDKApiClient(Builder builder) {
    sContext = builder.context;
    mEnvironment = builder.environment;
    configureConnectionTimeout(builder.connectionTimeout);
    setActiveEndPointUrl();
  }

  public Environment getEnvironment() {
    return mEnvironment;
  }

  public static WeakReference<Context> getContext() {
    return sContext;
  }

  private void configureConnectionTimeout(int timeoutMillis) {
    ConnectionData.setConnectionTimeout(timeoutMillis);
  }

  /**
   * Method to set end point url based on Environment {@link Environment}
   */
  private void setActiveEndPointUrl() {
    String url = (this.mEnvironment == Environment.PRODUCTION) ? ConnectionData.ENDPOINT_PRODUCTION
        : ConnectionData.ENDPOINT_SANDBOX;
    ConnectionData.setActiveEndPointUrl(url);
  }

  /**
   * API to get Encrypted token for given payment information.
   *
   * @param transactionObject encryption transaction object. In case of null,Throws {@link
   * NullPointerException}
   * @param callback callback for response of transaction
   * @return boolean, false if another transaction is already in progress.
   * @throws AcceptSDKException, If transactionObject is null. Also see {@link AcceptSDKException}
   */
  public boolean performEncryption(EncryptTransactionObject transactionObject,
      EncryptTransactionCallback callback) throws AcceptSDKException {
    if (transactionObject == null) {
      throw new AcceptSDKException(AcceptSDKException.NULL_TRANSACTION_OBJECT);
    }
    return AcceptSDKCore.getInstance().performEncryption(transactionObject, callback);
  }

  /**
   * Builder pattern to create ApiClient.
   */
  public static class Builder {
    private final WeakReference<Context> context;
    private final Environment environment;
    private int connectionTimeout;

    /**
     * API to build AcceptSDKpiClient
     *
     * @param context activity from where api is getting called.
     * @param environment sandbox or production. If this parameter is null by default environment
     * is sandbox.
     * @throws AcceptSDKException, If context is null. Also see {@link AcceptSDKException}
     */

    public Builder(Context context, Environment environment) throws AcceptSDKException {
      if (context == null) throw new AcceptSDKException(AcceptSDKException.NULL_CONTEXT);
      this.context = new WeakReference<>(context);
      this.environment = (environment == null) ? Environment.SANDBOX : environment;
    }

    /**
     * API to set ConnectionTimeout for Network calls
     *
     * @param timeout, time in milli seconds
     * @return AcceptSDKApiClient.Builder
     */
    public AcceptSDKApiClient.Builder setConnectionTimeout(int timeout) {
      this.connectionTimeout = timeout;
      return this;
    }

    public AcceptSDKApiClient build() {
      return new AcceptSDKApiClient(this);
    }
  }
}