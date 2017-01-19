package customer.glympse.glympse.data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.List;
import java.util.Map;

import customer.glympse.glympse.R;
import customer.glympse.glympse.app.LoginActivity;
import customer.glympse.glympse.app.MainActivity;
import customer.glympse.glympse.app.RegisterActivity;
import customer.glympse.glympse.data.local.PrefsHelper;
import customer.glympse.glympse.data.model.ApiResponse;
import customer.glympse.glympse.data.model.CustomerLocation;
import customer.glympse.glympse.data.model.MechanicDetail;
import customer.glympse.glympse.data.model.Provider;
import customer.glympse.glympse.data.model.UserInfo;
import customer.glympse.glympse.data.remote.GlympseService;
import customer.glympse.glympse.fragment.MyProfileFragment;
import customer.glympse.glympse.fragment.ServiceHistoryFragment;
import customer.glympse.glympse.fragment.SupportFragment;
import customer.glympse.glympse.fragment.TermsNConditionDialogFragment;
import customer.glympse.glympse.utils.ApplicationMetadata;
import customer.glympse.glympse.utils.DialogFactory;
import customer.glympse.glympse.utils.NetworkUtil;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin on 11/25/2016.
 */

public class DataManager {
    private static final String TAG = DataManager.class.getSimpleName();
    private GlympseService mApiService;
    private PrefsHelper prefsHelper;
    private Context mContext;
    private RequestCallback mCallback = null;
    private NearByProvidersCallback nearByProvidersCallback = null;
    private LocationUpdateCallback mLocationUpdateCallback = null;
    public DataManager(Context context) {
        mContext = context;
        mApiService = GlympseService.Factory.makeFairRepairService(context);
        prefsHelper = new PrefsHelper(context);
    }

    public void getHistory(Map<String, String> requestParams) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.getHistory(requestParams);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {

                    /*List<Provider> providerList = response.body().getResponseData().getAllProviders();
                    nearByProvidersCallback.allProviders(providerList);*/
                    mCallback.Data(response.body().getResponseData().getServiceHistory());


                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    public interface RequestCallback{
        void Data(Object data);
    }
    public void setCallback(RequestCallback mCallback){
        this.mCallback = mCallback;
    }

    public interface NearByProvidersCallback {
        void allProviders(List<Provider> providerList);
    }

    public interface LocationUpdateCallback{
        void locationReceived(CustomerLocation location);
    }
    public void setmLocationUpdateCallback(LocationUpdateCallback callback) {
        this.mLocationUpdateCallback = callback;
    }

    public void setNearByProvidersCallback(NearByProvidersCallback callback) {
        nearByProvidersCallback = callback;
    }
    public void signUp(final Map<String, RequestBody> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.signUp(requestMap);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();

                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    String sessionToken = response.body().getResponseData().getSessionToken();
                    UserInfo userInfo = response.body().getResponseData().getUserInfo();
                    prefsHelper.savePref(ApplicationMetadata.SESSION_TOKEN, sessionToken);
                    prefsHelper.savePref(ApplicationMetadata.USER_ID, userInfo.getId());
                    prefsHelper.savePref(ApplicationMetadata.USER_NAME, userInfo.getName());
                    prefsHelper.savePref(ApplicationMetadata.USER_EMAIL, userInfo.getEmail());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOBILE, userInfo.getPhoneNo());
                    prefsHelper.savePref(ApplicationMetadata.PASSWORD, userInfo.getPassword());
                    prefsHelper.savePref(ApplicationMetadata.USER_IMAGE, userInfo.getProfilePic());
                    prefsHelper.savePref(ApplicationMetadata.ADDRESS, userInfo.getAddress());
                    prefsHelper.savePref(ApplicationMetadata.USER_LATITUDE, userInfo.getLatitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_LONGITUDE, userInfo.getLongitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_ADD_DATE, userInfo.getAddDate());
                    prefsHelper.savePref(ApplicationMetadata.AVG_RATING,userInfo.getAvgRating());
                    prefsHelper.savePref(ApplicationMetadata.LOGIN, true);

                    //launch home screen activity
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //login
    public void login(final Map<String, String> loginRequest) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.login(loginRequest);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    String sessionToken = response.body().getResponseData().getSessionToken();
                    UserInfo userInfo = response.body().getResponseData().getUserInfo();
                    prefsHelper.savePref(ApplicationMetadata.SESSION_TOKEN, sessionToken);
                    prefsHelper.savePref(ApplicationMetadata.USER_ID, userInfo.getId());
                    prefsHelper.savePref(ApplicationMetadata.USER_NAME, userInfo.getName());
                    prefsHelper.savePref(ApplicationMetadata.USER_EMAIL, userInfo.getEmail());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOBILE, userInfo.getPhoneNo());
                    prefsHelper.savePref(ApplicationMetadata.PASSWORD, userInfo.getPassword());
                    prefsHelper.savePref(ApplicationMetadata.USER_IMAGE, userInfo.getProfilePic());
                    prefsHelper.savePref(ApplicationMetadata.ADDRESS, userInfo.getAddress());
                    prefsHelper.savePref(ApplicationMetadata.USER_LATITUDE, userInfo.getLatitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_LONGITUDE, userInfo.getLongitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_ADD_DATE, userInfo.getAddDate());
                    prefsHelper.savePref(ApplicationMetadata.AVG_RATING,userInfo.getAvgRating());
                    prefsHelper.savePref(ApplicationMetadata.LOGIN, true);

                    //launch home screen activity
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                progressDialog.dismiss();
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error);
            }
        });
    }

    //forgot password
    public void forgotPassword(final Map<String,String> forgotPasswordRequest) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention,R.string.no_connectin).show();
            return;
        }
        final ProgressDialog  progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.forgotPassword(forgotPasswordRequest);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse>call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.title_password_changed, response.body().getResponseMsg()).show();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                progressDialog.dismiss();
                DialogFactory.createSimpleOkErrorDialog(mContext,R.string.title_attention,R.string.msg_server_error);
            }
        });
    }

    //Logout user
    public void logout(final Map<String, String> logoutRequest) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.logout(logoutRequest);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    PrefsHelper prefsHelper = new PrefsHelper(mContext);
                    String deviveToken = prefsHelper.getPref(ApplicationMetadata.DEVICE_TOKEN);
                    prefsHelper.clearAllPref();
                    prefsHelper.savePref(ApplicationMetadata.DEVICE_TOKEN, deviveToken);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                progressDialog.dismiss();
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error);
            }
        });
    }

    //get static content
    public void getStaticPages(Map<String, String> requestMap, final String type) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.getStaticPages(requestMap);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    String content = response.body().getResponseData().getPage().getPagesDesc();

                    if (type.equals(ApplicationMetadata.ABOUT_CUSTOMER)) {
                        //launch about us fragment
                        Fragment newFragment = SupportFragment.newInstance(content);
                        ((MainActivity)mContext).addFragmentToStack(newFragment, "support");
                    } else if (type.equals(ApplicationMetadata.TNC_CUSTOMER)) {
                        //show tnc dialog
                        DialogFragment customerDetailFragment = TermsNConditionDialogFragment.newInstance(content);
                        customerDetailFragment.show(((RegisterActivity)mContext).getSupportFragmentManager(), "terms_n_condition");
                    }
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //get profile of the user
    public void getProfile(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.getProfile(requestMap);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    UserInfo userInfo = response.body().getResponseData().getUserInfo();
                    prefsHelper.savePref(ApplicationMetadata.USER_ID, userInfo.getId());
                    prefsHelper.savePref(ApplicationMetadata.USER_NAME, userInfo.getName());
                    prefsHelper.savePref(ApplicationMetadata.USER_EMAIL, userInfo.getEmail());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOBILE, userInfo.getPhoneNo());
                    prefsHelper.savePref(ApplicationMetadata.PASSWORD, userInfo.getPassword());
                    prefsHelper.savePref(ApplicationMetadata.USER_IMAGE, userInfo.getProfilePic());
                    prefsHelper.savePref(ApplicationMetadata.ADDRESS, userInfo.getAddress());
                    prefsHelper.savePref(ApplicationMetadata.USER_LATITUDE, userInfo.getLatitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_LONGITUDE, userInfo.getLongitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_ADD_DATE, userInfo.getAddDate());
                    prefsHelper.savePref(ApplicationMetadata.AVG_RATING,userInfo.getAvgRating());
                    Fragment newFragment = MyProfileFragment.newInstance(2);
                    ((MainActivity)mContext).addFragmentToStack(newFragment, "my_profile");
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //edit profile of the user
    public void editProfile(Map<String, RequestBody> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.editProfile(requestMap);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    UserInfo userInfo = response.body().getResponseData().getUserInfo();
                    prefsHelper.savePref(ApplicationMetadata.USER_ID, userInfo.getId());
                    prefsHelper.savePref(ApplicationMetadata.USER_NAME, userInfo.getName());
                    prefsHelper.savePref(ApplicationMetadata.USER_EMAIL, userInfo.getEmail());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOBILE, userInfo.getPhoneNo());
                    prefsHelper.savePref(ApplicationMetadata.PASSWORD, userInfo.getPassword());
                    prefsHelper.savePref(ApplicationMetadata.USER_IMAGE, userInfo.getProfilePic());
                    prefsHelper.savePref(ApplicationMetadata.ADDRESS, userInfo.getAddress());
                    prefsHelper.savePref(ApplicationMetadata.USER_LATITUDE, userInfo.getLatitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_LONGITUDE, userInfo.getLongitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_ADD_DATE, userInfo.getAddDate());

                    /*Fragment newFragment = MyProfileFragment.newInstance(2);
                    ((MainActivity)mContext).addFragmentToStack(newFragment, "my_profile");
                    ((MainActivity)mContext).loadData();*/

                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //reset password
    public void resetPassword(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.resetPassword(requestMap);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();

                    Intent intent = new Intent(mContext,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // get all services for customer
    public void getNearbyProviders(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.getNearbyProviders(requestMap);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {

                    List<Provider> providerList = response.body().getResponseData().getAllProviders();
                    nearByProvidersCallback.allProviders(providerList);

                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // send request to the mechs
    public void sendRequest(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.getSendRequest(requestMap);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    if (response.body().getResponseKey().equals(ApplicationMetadata.REQ_SEND_SUCCESS)) {
                        mCallback.Data("request_complete"+":"+response.body().getResponseData().getApp_request_id());
                    } else {
                        DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.title_success, response.body().getResponseMsg()).show();
                        mCallback.Data("no_mech_found");

                    }
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                    mCallback.Data("error");
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                mCallback.Data(new Object());
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // Get mechanic detail
    public void getMechanicDetail(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.getMechanicDetail(requestMap);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    MechanicDetail mechanicDetail = response.body().getResponseData().getServiceProvider();
                    mCallback.Data(mechanicDetail);
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // Add reivew for the mechanic
    public void rateMechanic(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.rateMechanic(requestMap);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.title_success, response.body().getResponseMsg()).show();
                    mCallback.Data(new Object());
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // Accept offer by the client
    public void acceptOffer(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.acceptOffer(requestMap);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    mCallback.Data(new Object());
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // Cancel mechanic request
    public void cancelRequest(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<ApiResponse> call = mApiService.cancelRequest(requestMap);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    mCallback.Data(new Object());
                    DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.title_success, response.body().getResponseMsg()).show();
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //update lat and lng
    //get customer
    public void getLatLng(final Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        /*final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        */Call<ApiResponse> call = mApiService.getLatLng(requestMap);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.body() == null) {
                    //progressDialog.dismiss();
                    return;
                }
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    mLocationUpdateCallback.locationReceived(response.body().getCustomerLocation());
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                //progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Log error here since request failed
//                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }
}
