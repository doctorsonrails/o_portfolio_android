package com.oportfolio.oportfolio.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class opAuthenticator extends AbstractAccountAuthenticator {

	private Context mContext = null;
	public opAuthenticator(Context context) {
		super(context);
		mContext = context;
	}
	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		final Intent intent = new Intent(mContext, opAuthenticatorActivity.class);
        intent.putExtra(opAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
	}
	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) throws NetworkErrorException {
		  if (options != null && options.containsKey(AccountManager.KEY_PASSWORD)) {
	            final String password = options.getString(AccountManager.KEY_PASSWORD);
	            final boolean verified = onlineConfirmPassword(account.name, password);
	            final Bundle result = new Bundle();
	            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, verified);
	            return result;
	        }
		   
	        // Launch AuthenticatorActivity to confirm credentials
	        final Intent intent = new Intent(mContext, opAuthenticatorActivity.class);
	        intent.putExtra(opAuthenticatorActivity.PARAM_USERNAME, account.name);
	        intent.putExtra(opAuthenticatorActivity.PARAM_CONFIRMCREDENTIALS, true);
	        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
	        final Bundle bundle = new Bundle();
	        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
	        return bundle;
	}
	
	 /**
     * Validates user's password on the server
     */
    private boolean onlineConfirmPassword(final String username, final String password) {
    	
    	
//    	Authenticator.setDefault(new Authenticator() {
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication(username, password.toCharArray());
//
//			}
//		});
//    	
//    	
//    	
//    	final String loginUrl = "http://test.dev.magicblox.com/drupal/rest/user/login";
//    	
//    	
//    	List<NameValuePair> bodyParameters = new ArrayList<NameValuePair>();
//    	bodyParameters.add(new BasicNameValuePair("username", username));
//    	bodyParameters.add(new BasicNameValuePair("password", password));
//    	
//    	HttpResponse response = HttpManager.Post(loginUrl, null, null, bodyParameters, 0);
//    	
//    	if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
//
//			HttpEntity entity = response.getEntity();
//
//			if (entity != null) {
//				String content = EntityUtils.toString(entity);
//				System.out.println(content);
//				System.out.println("----------------------------------------");
//				System.out.println("Uncompressed size: "+content.length());
//			}
//    	}
        return true;
    }
	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getAuthTokenLabel(String authTokenType) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	
}
