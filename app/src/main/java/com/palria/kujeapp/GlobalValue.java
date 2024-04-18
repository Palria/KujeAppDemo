


package com.palria.kujeapp;



import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.HashBiMap;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.palria.kujeapp.adapters.AdvertsDataModel;
import com.palria.kujeapp.models.AnswerDataModel;
import com.palria.kujeapp.models.CommentDataModel;
import com.palria.kujeapp.models.ServiceDataModel;
import com.palria.kujeapp.models.WelcomeScreenItemModal;
import com.palria.kujeapp.widgets.BottomSheetFormBuilderWidget;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class GlobalValue {

    public interface OnMenuItemClickListener {
        boolean onMenuItemClicked(MenuItem item);

    }
    public interface OnDocumentExistStatusCallback{

        void onExist();
        void onNotExist();
        void onFailed(@NonNull String errorMessage);

    }

    public interface OnDeleteDocumentListener {
        void onDeleteDocumentSuccess(Void unused);

        void onDeleteDocumentFailed(@NonNull Exception e);

    }
    public  interface OnDeleteImageListener {
        void onDeleteImageSuccess(Void unused);

        void onDeleteImageFailed(@NonNull Exception e);

    }

    public interface AnswerCallback{

        void onImageGallerySelected(ImageView imageView);
        void onCameraSelected(ImageView imageView);
        void onVideoGallery(View view);
        void onStart(String answerId);
        void onSuccess(String answerId);
        void onFailed(String errorMessage);

    }
    public interface ActionCallback{
        void onSuccess();
        void onFailed(String errorMessage);
    }

    interface OnGenerateMessageDocumentDirectoryReferenceListener{
        void onGenerateSuccess(DocumentReference messageDocumentDirectoryReference);
        void onGenerateFailed( String errorMessage);
    }
    static OnRefreshChatsTriggeredListener onRefreshChatsTriggeredListener;

    interface OnMessageImageUploadListener{

        void onUploadSuccess(String messageId,String messageImageDownloadUrl, String messageImageStorageReference);
        void onUploadFailed(String errorMessage);
    }

    public static  String businessId = "eqUm0mBaLZcH7qIWt8qwZbIptSC2";

    //TODO initialize this platform account id later.
    private static  String PLATFORM_ACCOUNT_ID = "0";
    public static boolean isOwner = true;
    public static boolean isWorker;
    public  static boolean isCustomer;

    static ImageView recipientImageView;

    private static String CURRENT_USER_ID;
    private static String CURRENT_USER_TOKEN_ID = "EMPTY";

    private static FirebaseFirestore firebaseFirestoreInstance;
    private static FirebaseStorage firebaseStorageInstance;

    private static ArrayList<String> JOB_NOTIFICATION_LISTENERS_ID_ARRAY_LIST = new ArrayList<>();
    private static ArrayList<String> PRODUCT_NOTIFICATION_LISTENERS_ID_ARRAY_LIST = new ArrayList<>();




    public static final String PLATFORM_CONFIGURATION_FILE = "PLATFORM_CONFIGURATION_FILE";

    public static final String USERS_ONLINE_LIST = "USERS_ONLINE_LIST";
    public static final String DATE_USERS_ONLINE_LIST = "DATE_USERS_ONLINE_LIST";
    public static final String IS_INDICATED_AS_ONLINE = "IS_INDICATED_AS_ONLINE";
    public static final String TOTAL_NUMBER_OF_USERS_ONLINE = "TOTAL_NUMBER_OF_USERS_ONLINE";


    public static final String MALE = "MALE";
    public static final String FEMALE = "FEMALE";
    public static final String OTHER = "OTHER";


    public static final String ALL_USERS = "ALL_USERS";
    public static final String ALL_PAGES = "ALL_PAGES";
    public static final String PAGE_POSTER_ID = "PAGE_POSTER_ID";
    public static final String USER_DISPLAY_NAME = "USER_DISPLAY_NAME";
    public static final String IS_ACCOUNT_VERIFIED = "IS_ACCOUNT_VERIFIED";
    public static final String USER_DESCRIPTION = "USER_DESCRIPTION";
    public static final String USER_BIRTH_DATE = "USER_BIRTH_DATE";
    public static final String USER_CONTACT_EMAIL_ADDRESS = "USER_CONTACT_EMAIL_ADDRESS";
    public static final String USER_PERSONAL_WEBSITE_LINK = "USER_PERSONAL_WEBSITE_LINK";
    public static final String USER_PROFILE_PHOTO_DOWNLOAD_URL = "USER_PROFILE_PHOTO_DOWNLOAD_URL";
    public static final String LAST_DATE_USER_REGISTERED_TIME_STAMP = "LAST_DATE_USER_REGISTERED_TIME_STAMP";
    public static final String TOTAL_NUMBER_OF_USERS = "TOTAL_NUMBER_OF_USERS";
    public static final String TOTAL_NUMBER_OF_MALE_USERS = "TOTAL_NUMBER_OF_MALE_USERS";
    public static final String TOTAL_NUMBER_OF_FEMALE_USERS = "TOTAL_NUMBER_OF_FEMALE_USERS";
    public static final String TOTAL_NUMBER_OF_OTHER_USERS = "TOTAL_NUMBER_OF_OTHER_USERS";
    public static final String USER_EMAIL_ADDRESS_LIST = "USER_EMAIL_ADDRESS_LIST";
    public static final String USER_COVER_PHOTO_DOWNLOAD_URL = "USER_COVER_PHOTO_DOWNLOAD_URL";
    public static final String USER_COUNTRY_OF_RESIDENCE = "USER_COUNTRY_OF_RESIDENCE";
    public static final String USER_GENDER_TYPE = "USER_GENDER_TYPE";
    public static final String USER_EMAIL_ADDRESS = "USER_EMAIL_ADDRESS";
    public static final String IS_USER_PROFILE_PHOTO_INCLUDED = "IS_USER_PROFILE_PHOTO_INCLUDED";
    public static final String USER_CONTACT_PHONE_NUMBER = "USER_CONTACT_PHONE_NUMBER";
    public static final String USER_PROFILE_PHOTO = "USER_PROFILE_PHOTO";
    public static final String USER_PROFILE_DATE_EDITED = "USER_PROFILE_DATE_EDITED";
    public static final String USER_PROFILE_PHOTO_STORAGE_REFERENCE = "USER_PROFILE_PHOTO_STORAGE_REFERENCE";
    public static final String USER_IMAGES = "USER_IMAGES";
    public static final String IS_USER_BLOCKED = "IS_USER_BLOCKED";
    public static final String USER_PROFILE_DATE_CREATED_TIME_STAMP = "USER_PROFILE_DATE_CREATED_TIME_STAMP";
    public static final String USER_PROFILE_DATE_EDITED_TIME_STAMP = "USER_PROFILE_DATE_EDITED_TIME_STAMP";
    public static final String USER_TOKEN_ID = "USER_TOKEN_ID";
    public static final String USER_SEARCH_VERBATIM_KEYWORD = "USER_SEARCH_VERBATIM_KEYWORD";
    public static final String USER_SEARCH_ANY_MATCH_KEYWORD = "USER_SEARCH_ANY_MATCH_KEYWORD";
    public static final String ALL_AVAILABLE_UPDATES = "ALL_AVAILABLE_UPDATES";
    public static final String BUSINESS_ADMIN_ID = "BUSINESS_ADMIN_ID";
    public static final String PRODUCT_TITLE = "PRODUCT_TITLE";
    public static final String PRODUCT_CATEGORY = "PRODUCT_CATEGORY";
    public static final String PRODUCT_PRICE = "PRODUCT_PRICE";
    public static final String UPDATES = "UPDATES";
    public static final String UPDATE_PROFILE = "UPDATE_PROFILE";
    public  static final String PRODUCT_COLLECTION_NAME = "PRODUCT_COLLECTION_NAME";


    public static final String PRODUCT_ID = "PRODUCT_ID";
    public static final String TOTAL_NUMBER_OF_ORDERS = "TOTAL_NUMBER_OF_ORDERS";
    public static final String TOTAL_NUMBER_OF_MY_ORDERS = "TOTAL_NUMBER_OF_MY_ORDERS";
    public static final String TOTAL_NUMBER_OF_NEW_ORDERS = "TOTAL_NUMBER_OF_NEW_ORDERS";
    public static final String TOTAL_NUMBER_OF_VIEWS = "TOTAL_NUMBER_OF_VIEWS";
    public static final String ALL_VIEWS = "ALL_VIEWS";
    public  static final String PRODUCT_DATA_MODEL = "PRODUCT_DATA_MODEL";
    public static final String PRODUCT_IMAGE_DOWNLOAD_URL = "PRODUCT_IMAGE_DOWNLOAD_URL";
    public static final String PRODUCT_DESCRIPTION = "PRODUCT_DESCRIPTION";
    public static final String DATE_POSTED_TIME_STAMP = "DATE_POSTED_TIME_STAMP";
    public static final String DATE_SUBMITTED_TIME_STAMP = "DATE_SUBMITTED_TIME_STAMP";
    public static final String IS_APPROVED = "IS_APPROVED";
    public static final String DATE_TIME_STAMP_APPROVED = "DATE_TIME_STAMP_APPROVED";
    public static final String IS_FROM_SUBMISSION = "IS_FROM_SUBMISSION";
    public static final String PRODUCT_IMAGE_DOWNLOAD_URL_ARRAY_LIST = "PRODUCT_IMAGE_DOWNLOAD_URL_ARRAY_LIST";
    public static final String LOCATION = "LOCATION";
    public  static final String IS_NEW = "IS_NEW";
    public  static final String DATE_TIME_STAMP_SOLD = "DATE_TIME_STAMP_SOLD";
    public  static final String IS_SOLD = "IS_SOLD";
    public  static final String IS_PRIVATE = "IS_PRIVATE";
    public  static final String IS_OWNER_SEEN = "IS_OWNER_SEEN";
    public  static final String DATE_SEEN_LAST_TIME_STAMP = "DATE_SEEN_LAST_TIME_STAMP";
    public  static final String DATE_SEEN = "DATE_SEEN";
    public static final String IS_BLOCKED = "IS_BLOCKED";
    public static final String PRODUCT_OWNER_USER_ID = "POST_OWNER_USER_ID";
    public static final String IS_FROM_SINGLE_OWNER = "IS_FROM_SINGLE_OWNER";
    public static final String PRODUCTS = "PRODUCTS";
    public static final String ALL_PRODUCTS = "ALL_PRODUCTS";
    public static final String IS_EDITION = "IS_EDITION";
    public static final String SEARCH_ANY_MATCH_KEYWORD = "SEARCH_ANY_MATCH_KEYWORD";
    public static final String SEARCH_VERBATIM_KEYWORD = "SEARCH_VERBATIM_KEYWORD";

    public static final String ALL_ORDERS = "ALL_ORDERS";
    public static final String IS_ORDER_RESOLVED = "IS_ORDER_RESOLVED";
    public  static final String LAST_ORDER_ID = "LAST_ORDER_ID";
    public  static final String LAST_ORDER_DATE_TIME_STAMP = "LAST_ORDER_DATE_TIME_STAMP";
    public  static final String LAST_PRODUCT_ORDERED_ID_TIME_STAMP = "LAST_PRODUCT_ORDERED_ID_TIME_STAMP";
    public  static final String LAST_PRODUCT_ORDERED_ID = "LAST_PRODUCT_ORDERED_ID";
    public  static final String DATE_ORDERED_TIME_STAMP = "DATE_ORDERED_TIME_STAMP";
    public  static final String DATE_ORDERED = "DATE_ORDERED";
    public  static final String CUSTOMER_NAME = "CUSTOMER_NAME";
    public  static final String CUSTOMER_CONTACT_PHONE_NUMBER = "CUSTOMER_CONTACT_PHONE_NUMBER";
    public  static final String CUSTOMER_CONTACT_EMAIL = "CUSTOMER_CONTACT_EMAIL";
    public static final String CUSTOMER_CONTACT_ADDRESS = "CUSTOMER_CONTACT_ADDRESS";
    public static final String CUSTOMER_CONTACT_LOCATION = "CUSTOMER_CONTACT_LOCATION";
    public static final String ORDER_DESCRIPTION = "ORDER_DESCRIPTION";
    public static final String ORDER_ID = "ORDER_ID";
    public static final String PRODUCT_PROFILE = "PRODUCT_PROFILE";
    public static final String IMAGES = "IMAGES";
    public static final String VIDEOS = "VIDEOS";
    public static final String IMAGE_DOWNLOAD_URL_1 = "IMAGE_DOWNLOAD_URL_1";
    public static final String IMAGE_DOWNLOAD_URL_2 = "IMAGE_DOWNLOAD_URL_2";
    public static final String IMAGE_DOWNLOAD_URL = "IMAGE_DOWNLOAD_URL";
    public static final String CUSTOMER_ID = "CUSTOMER_ID";
    public static final String IS_PRODUCT_SUBMISSION = "IS_PRODUCT_SUBMISSION";
    public static final String IS_PRODUCT_APPROVAL = "IS_PRODUCT_APPROVAL";



    public static final String IS_SINGLE_PRODUCT = "IS_SINGLE_PRODUCT";
    public static final String IS_SINGLE_CUSTOMER = "IS_SINGLE_CUSTOMER";
    public static final String IS_SINGLE_SERVICE = "IS_SINGLE_SERVICE";
    public static final String UPDATE_POSTER_ID = "UPDATE_POSTER_ID";
    public static final String TOTAL_NUMBER_OF_IMAGES_UPLOADED = "TOTAL_NUMBER_OF_IMAGES_UPLOADED";
    public static final String TIME_STAMP = "TIME_STAMP";
    public static final String TOTAL_NUMBER_OF_PRODUCTS = "TOTAL_NUMBER_OF_PRODUCTS";
    public static final String TOTAL_NUMBER_OF_PRODUCTS_SUBMITTED = "TOTAL_NUMBER_OF_PRODUCTS_SUBMITTED";
    public static final String PRODUCTS_ID_SUBMITTED_ARRAY_LIST = "PRODUCTS_ID_SUBMITTED_ARRAY_LIST";
    public static final String TOTAL_NUMBER_OF_UN_APPROVED_PRODUCTS = "TOTAL_NUMBER_OF_UN_APPROVED_PRODUCTS";
    public static final String LAST_PRODUCT_ID = "LAST_PRODUCT_ID";



    public static final String FRAGMENT_TYPE = "FRAGMENT_TYPE";
    public static final String USERS_FRAGMENT_TYPE = "USERS_FRAGMENT_TYPE";
    public static final String APPLICANT_FRAGMENT_TYPE = "APPLICANT_FRAGMENT_TYPE";
    public static final String USER_PRODUCT_FRAGMENT_TYPE = "USER_PRODUCT_FRAGMENT_TYPE";
    public static final String USER_PROFILE_FRAGMENT_TYPE = "USER_PROFILE_FRAGMENT_TYPE";
    public static final String ORDER_FRAGMENT_TYPE = "ORDER_FRAGMENT_TYPE";
    public static final String REQUEST_FRAGMENT_TYPE = "REQUEST_FRAGMENT_TYPE";
    public static final String PAGE_FRAGMENT_TYPE = "PAGE_FRAGMENT_TYPE";
    public static final String CUSTOMERS_FRAGMENT_TYPE = "CUSTOMERS_FRAGMENT_TYPE";
    public static final String NOTES_FRAGMENT_TYPE = "NOTES_FRAGMENT_TYPE";
    public static final String ANSWER_FRAGMENT_TYPE = "ANSWER_FRAGMENT_TYPE";
    public static final String COMMENT_FRAGMENT_TYPE = "COMMENT_FRAGMENT_TYPE";
    public static final String APPROVE_ADVERTS_FRAGMENT_TYPE = "APPROVE_ADVERTS_FRAGMENT_TYPE";

    public static final String USER_ID = "USER_ID";


    public static final String PLATFORM_NOTIFICATIONS = "PLATFORM_NOTIFICATIONS";
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";
    public static final String NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";
    public static final String NOTIFICATION_VIEWERS_LIST = "NOTIFICATION_VIEWERS_LIST";
    public static final String NOTIFICATIONS_SEEN = "NOTIFICATIONS_SEEN";
    public static final String DATE_NOTIFIED_TIME_STAMP = "DATE_NOTIFIED_TIME_STAMP";
    public static final String NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
    public static final String NOTIFICATION_SENDER_ID = "NOTIFICATION_SENDER_ID";
    public static final String IS_SEEN = "IS_SEEN";
    public static final String NOTIFICATION_MODEL_INFO_LIST = "NOTIFICATION_MODEL_INFO_LIST";
    public static final String PERSONALIZED_NOTIFICATIONS = "PERSONALIZED_NOTIFICATIONS";
    public static final String NOTIFICATION_TYPE_QUIZ = "NOTIFICATION_TYPE_QUIZ";
    public static final String DATE_PERSONALIZED_NOTIFICATION_LAST_SEEN_TIME_STAMP = "DATE_PERSONALIZED_NOTIFICATION_LAST_SEEN_TIME_STAMP";
    public static final String THERE_IS_NEW_PERSONALIZED_NOTIFICATION = "THERE_IS_NEW_PERSONALIZED_NOTIFICATION";

    public static final String NOTIFICATION_TYPE_PRODUCT_ORDERED = "NOTIFICATION_TYPE_PRODUCT_ORDERED";
    public static final String NOTIFICATION_TYPE_ADVERT_SUBMITTED = "NOTIFICATION_TYPE_ADVERT_SUBMITTED";
    public static final String NOTIFICATION_TYPE_JOB_POSTED = "NOTIFICATION_TYPE_JOB_POSTED";
    public static final String NOTIFICATION_TYPE_PRODUCT_POSTED = "NOTIFICATION_TYPE_PRODUCT_POSTED";
    public static final String NOTIFICATION_TYPE_ADVERT_APPROVED = "NOTIFICATION_TYPE_ADVERT_APPROVED";
    public static final String NOTIFICATION_TYPE_ADVERT_DECLINED = "NOTIFICATION_TYPE_ADVERT_DECLINED";
    public static final String NOTIFICATION_TYPE_ADVERT_COMPLETED = "NOTIFICATION_TYPE_ADVERT_COMPLETED";

    public static final String JOB_NOTIFICATION_LISTENERS_ID_LIST = "JOB_NOTIFICATION_LISTENERS_ID_LIST";
    public static final String PRODUCT_NOTIFICATION_LISTENERS_ID_LIST = "PRODUCT_NOTIFICATION_LISTENERS_ID_LIST";


    public static final String PAGES = "PAGES";
    public static final String PAGE_ID = "PAGE_ID";
    public static final String PAGE_TITLE = "PAGE_TITLE";
    public static final String PAGE_DESCRIPTION = "PAGE_DESCRIPTION";
    public static final String PAGE_REQUESTS = "PAGE_REQUESTS";
    public static final String IS_PAGE_REQUEST_RESOLVED = "IS_PAGE_REQUEST_RESOLVED";
    public static final String TOTAL_PAGE_REQUESTS = "TOTAL_PAGE_REQUESTS";
    public static final String TOTAL_NEW_PAGE_REQUESTS = "TOTAL_NEW_PAGE_REQUESTS";
    public static final String PAGE_OWNER_USER_ID = "PAGE_OWNER_USER_ID";
    public static final String TOTAL_NUMBER_OF_PAGES = "TOTAL_NUMBER_OF_PAGES";
    public static final String LAST_PAGE_ID = "LAST_PAGE_ID";
    public static final String REQUEST_ID = "REQUEST_ID";
    public static final String ALL_REQUESTS = "ALL_REQUESTS";
    public static final String REQUEST_DESCRIPTION = "REQUEST_DESCRIPTION";
    public static final String DATE_REQUESTED_TIME_STAMP = "DATE_REQUESTED_TIME_STAMP";
    public static final String LAST_REQUEST_DATE_TIME_STAMP = "LAST_REQUEST_DATE_TIME_STAMP";
    public static final String LAST_PAGE_REQUESTED_ID = "LAST_PAGE_REQUESTED_ID";
    public static final String LAST_REQUEST_ID = "LAST_REQUEST_ID";
    public static final String PAGE_DATA_MODEL = "PAGE_DATA_MODEL";
    public static final String DATE_ADDED_TIME_STAMP = "DATE_ADDED_TIME_STAMP";
    public static final String DATE_EDITED_TIME_STAMP = "DATE_EDITED_TIME_STAMP";
    public static final String DATE_CREATED_TIME_STAMP = "DATE_CREATED_TIME_STAMP";


    public static final String PLATFORM_NOTES = "PLATFORM_NOTES";
    public static final String NOTE_ID = "NOTE_ID";
    public static final String NOTE_TITLE = "NOTE_TITLE";
    public static final String NOTE_BODY = "NOTE_BODY";


    public static final String PROVISION_SHOP = "PROVISION SHOP";
    public static final String SECONDARY_SCHOOL = "SECONDARY SCHOOL";
    public static final String PRIMARY_SCHOOL = "PRIMARY SCHOOL";
    public static final String HOTEL = "HOTEL";
    public static final String RESTAURANT = "RESTAURANT";
    public static final String HIGHER_INSTITUTION = "HIGHER INSTITUTION";
    public static final String CYBER_CAFE = "CYBER CAFE";
    public static final String COMPUTER_SHOP = "COMPUTER SHOP";

    public static final String PAGE_CATALOG = "PAGE_CATALOG";
    public static final String CATALOG_TITLE = "CATALOG_TITLE";
    public static final String CATALOG_ID = "CATALOG_ID";
    public static final String CATALOG_DESCRIPTION = "CATALOG_DESCRIPTION";
    public static final String TOTAL_NUMBER_OF_CATALOG = "TOTAL_NUMBER_OF_CATALOG";
    public static final String CATALOG_COVER_PHOTO_DOWNLOAD_URL = "CATALOG_COVER_PHOTO_DOWNLOAD_URL";



    public static final String PLATFORM_SALES_RECORD = "PLATFORM_SALES_RECORD";
    public static final String TOTAL_NUMBER_OF_RECORD_CONTENTS = "TOTAL_NUMBER_OF_RECORD_CONTENTS";
    public static final String CELL_RECORD_CONTENT_LIST_ = "CELL_RECORD_CONTENT_LIST_";
    public static final String RECORD_ID = "RECORD_ID";
    public static final String RECORD_TITLE = "RECORD_TITLE";
    public static final String RECORD_CAPTION = "RECORD_CAPTION";



    public static final String IS_FROM_SEARCH_CONTEXT = "IS_FROM_SEARCH_CONTEXT";
    public static final String SEARCH_KEYWORD = "SEARCH_KEYWORD";


    public static final String DATE_SENT_TIME_STAMP = "DATE_SENT_TIME_STAMP";
    public static final String LAST_SENT_TIME_STAMP = "LAST_SENT_TIME_STAMP";


    public static final String PLATFORM_UPDATES = "PLATFORM_UPDATES";
    public static final String UPDATE_ID = "UPDATE_ID";
    public static final String UPDATE_DATA_MODEL = "UPDATE_DATA_MODEL";
    public static final String UPDATE_TITLE = "UPDATE_TITLE";
    public static final String UPDATE_DESCRIPTION = "UPDATE_DESCRIPTION";
    public static final String LAST_UPDATE_ID = "LAST_UPDATE_ID";
    public static final String TOTAL_NUMBER_OF_UPDATES = "TOTAL_NUMBER_OF_UPDATES";
    public static final String UPDATE_IMAGE_DOWNLOAD_URL_ARRAY_LIST = "UPDATE_IMAGE_DOWNLOAD_URL_ARRAY_LIST";
    public static final String UPDATE_VIEWERS_ID_ARRAY_LIST = "UPDATE_VIEWERS_ID_ARRAY_LIST";



    public static final String PLATFORM_ADVERTS = "PLATFORM_ADVERTS";
    public static final String ADVERT_ID = "ADVERT_ID";
    public static final String ADVERT_DATA_MODEL = "ADVERT_DATA_MODEL";
    public static final String ADVERT_TITLE = "ADVERT_TITLE";
    public static final String ADVERT_VIEW_LIMIT = "ADVERT_VIEW_LIMIT";
    public static final String IS_VIEW_EXCEEDED = "IS_VIEW_EXCEEDED";
    public static final String DATE_VIEW_LIMIT_EXCEEDED_TIME_STAMP = "DATE_VIEW_LIMIT_EXCEEDED_TIME_STAMP";
    public static final String ADVERT_DESCRIPTION = "UPDATE_DESCRIPTION";
    public static final String TOTAL_NUMBER_OF_ADVERTS = "TOTAL_NUMBER_OF_ADVERTS";
    public static final String ADVERT_IMAGE_DOWNLOAD_URL_ARRAY_LIST = "ADVERT_IMAGE_DOWNLOAD_URL_ARRAY_LIST";
    public static final String ADVERT_VIEWERS_ID_ARRAY_LIST = "ADVERT_VIEWERS_ID_ARRAY_LIST";
    public static final String LAST_ADVERT_ID = "LAST_ADVERT_ID";
    public static final String IS_FOR_APPROVAL = "IS_FOR_APPROVAL";
    public static final String IS_ADVERT_REQUESTED = "IS_ADVERT_REQUESTED";
    public static final String TOTAL_NUMBER_OF_REQUESTED_ADVERT_VIEW = "TOTAL_NUMBER_OF_REQUESTED_ADVERT_VIEW";

    public static final String ALL_ADVERTS = "ALL_ADVERTS";
    public static final String ADVERTS_ID_LIST = "ADVERTS_ID_LIST";
    public static final String CUSTOM_ADVERT_TYPE = "CUSTOM_ADVERT_TYPE";
    public static final String PRODUCT_ADVERT_TYPE = "PRODUCT_ADVERT_TYPE";
    public static final String BUSINESS_PAGE_ADVERT_TYPE = "BUSINESS_PAGE_ADVERT_TYPE";
    public static final String PEOPLE_ADVERT_TYPE = "PEOPLE_ADVERT_TYPE";
    public static final String IS_ADVERT_RUNNING = "IS_ADVERT_RUNNING";


    public static final String BUSINESS_OWNER_USER_ID = "BUSINESS_OWNER_USER_ID";


    public static final String ALL_JOBS = "ALL_JOBS";
    public static final String JOB_ID = "JOB_ID";
    public static final String JOB_OWNER_USER_ID = "JOB_OWNER_USER_ID";
    public static final String JOB_TITLE = "JOB_TITLE";
    public static final String CONTACT_PHONE_NUMBER = "CONTACT_PHONE_NUMBER";
    public static final String CONTACT_EMAIL = "CONTACT_EMAIL";
    public static final String CONTACT_ADDRESS = "CONTACT_ADDRESS";
    public static final String JOB_CATEGORY = "JOB_CATEGORY";
    public static final String JOB_IMAGE_DOWNLOAD_URL_ARRAY_LIST = "JOB_IMAGE_DOWNLOAD_URL_ARRAY_LIST";
    public static final String JOB_TYPE = "JOB_TYPE";
    public static final String JOB_SALARY = "JOB_SALARY";
    public static final String TOTAL_NUMBER_OF_NEW_APPLICANTS = "TOTAL_NUMBER_OF_NEW_APPLICANTS";
    public static final String TOTAL_NUMBER_OF_APPLICANTS = "TOTAL_NUMBER_OF_APPLICANTS";
    public static final String TOTAL_NUMBER_OF_JOBS = "TOTAL_NUMBER_OF_JOBS";
    public static final String LAST_JOB_ID = "LAST_JOB_ID";
    public static final String JOB_DESCRIPTION = "JOB_DESCRIPTION";
    public static final String IS_CLOSED = "IS_CLOSED";
    public static final String IS_PUBLIC = "IS_PUBLIC";
    public static final String DATE_TIME_STAMP_CLOSED = "DATE_TIME_STAMP_CLOSED";
    public static final String JOB_DATA_MODEL = "JOB_DATA_MODEL";
    public static final String JOB_IMAGE_DOWNLOAD_URL = "JOB_IMAGE_DOWNLOAD_URL";
    public static final String IS_SINGLE_JOB = "IS_SINGLE_JOB";
    public static final String LAST_JOB_APPLIED_ID = "LAST_JOB_APPLIED_ID";

    public static final String APPLICATION_ID = "APPLICATION_ID";
    public static final String APPLICANT_ID = "APPLICANT_ID";
    public static final String APPLICANT_CONTACT_EMAIL = "APPLICANT_CONTACT_EMAIL";
    public static final String APPLICANT_CONTACT_PHONE_NUMBER = "APPLICANT_CONTACT_PHONE_NUMBER";
    public static final String APPLICANT_CONTACT_ADDRESS = "APPLICANT_CONTACT_ADDRESS";
    public static final String DATE_APPLIED_TIME_STAMP = "DATE_APPLIED_TIME_STAMP";
    public static final String APPLICATION_DESCRIPTION = "APPLICATION_DESCRIPTION";
    public static final String TOTAL_NUMBER_OF_MY_APPLICATIONS = "TOTAL_NUMBER_OF_MY_APPLICATIONS";
    public static final String LAST_APPLICATION_ID = "LAST_APPLICATION_ID";
    public static final String LAST_APPLICATION_DATE_TIME_STAMP = "LAST_APPLICATION_DATE_TIME_STAMP";
    public static final String ALL_APPLICATIONS = "ALL_APPLICATIONS";
    public static final String IS_SINGLE_APPLICANT = "IS_SINGLE_APPLICANT";
    public static final String IS_RESOLVED = "IS_RESOLVED";



    public static final String QUESTION_ID = "QUESTION_ID";
    public static final String TOTAL_QUESTIONS = "TOTAL_QUESTIONS";
    public static final String CATEGORY = "CATEGORY";
    public static final String QUESTION_DATA_MODEL = "QUESTION_DATA_MODEL";
    public static final String QUESTION_BODY = "QUESTION_BODY";
    public static final String QUESTION_PHOTO_DOWNLOAD_URL = "QUESTION_PHOTO_DOWNLOAD_URL";
    public static final String ALL_QUESTIONS = "ALL_QUESTIONS";
    public static final String IS_PHOTO_INCLUDED = "IS_PHOTO_INCLUDED";
    public static final String PHOTOS = "PHOTOS";
    public static final String QUESTION_SEARCH_ANY_MATCH_KEYWORD = "QUESTION_SEARCH_ANY_MATCH_KEYWORD";
    public static final String TOTAL_NUMBER_OF_ANSWER = "TOTAL_NUMBER_OF_ANSWER";

    public static final String ALL_ANSWERS = "ALL_ANSWERS";
    public static final String ANSWER_ID = "ANSWER_ID";
    public static final String ANSWER_BODY = "ANSWER_BODY";
    public static final String ANSWER_PHOTO_DOWNLOAD_URL = "ANSWER_PHOTO_DOWNLOAD_URL";
    public static final String CONTRIBUTOR_ID = "CONTRIBUTOR_ID";
    public static final String ANSWER_CONTRIBUTORS_LIST = "ANSWER_CONTRIBUTORS_LIST";
    public static final String TOTAL_NUMBER_OF_ANSWER_CONTRIBUTED = "TOTAL_NUMBER_OF_ANSWER_CONTRIBUTED";
    public static final String QUESTIONS_ANSWERED_LIST = "QUESTIONS_ANSWERED_LIST";
    public static final String IS_FROM_QUESTION_CONTEXT = "IS_FROM_QUESTION_CONTEXT";
    public static final String IS_VIEW_ALL_ANSWERS_FOR_SINGLE_QUESTION = "IS_VIEW_ALL_ANSWERS_FOR_SINGLE_QUESTION";
    public static final String IS_VIEW_SINGLE_ANSWER_REPLY = "IS_VIEW_SINGLE_ANSWER_REPLY";
    public static final String PARENT_ID = "PARENT_ID";
    public static final String HAS_PARENT = "HAS_PARENT";
    public static final String IS_ANSWER = "IS_ANSWER";
    public static final String IS_HIDDEN_BY_CONTRIBUTOR = "IS_HIDDEN_BY_CONTRIBUTOR";
    public static final String TOTAL_UP_VOTES = "TOTAL_UP_VOTES";
    public static final String TOTAL_DOWN_VOTES = "TOTAL_DOWN_VOTES";
    public static final String UP_VOTERS_ID_LIST = "UP_VOTERS_ID_LIST";
    public static final String DOWN_VOTERS_ID_LIST = "DOWN_VOTERS_ID_LIST";
    public static final String TOTAL_REPLIES = "TOTAL_REPLIES";
    public static final String REPLY_CONTRIBUTORS_LIST = "REPLY_CONTRIBUTORS_LIST";
    public static final String HAS_REPLIES = "HAS_REPLIES";
    public static final String IS_HIDDEN_BY_AUTHOR = "IS_HIDDEN_BY_AUTHOR";
    public static final String REPLIERS_ID_LIST = "REPLIERS_ID_LIST";
    public static final String IS_FOR_PREVIEW = "IS_FOR_PREVIEW";



    public static final String AUTHOR_ID = "AUTHOR_ID";


    public static final String TOTAL_LIKES = "TOTAL_LIKES";
    public static final String TOTAL_COMMENTS = "TOTAL_COMMENTS";
    public static final String LIKED_UPDATES = "LIKED_UPDATES";
    public static final String LIKED_UPDATE_COMMENT_LIST = "LIKED_UPDATE_COMMENT_LIST";
    public static final String MY_UPDATES_COMMENT = "MY_UPDATES_COMMENT";
    public static final String COMMENT_ID = "COMMENT_ID";
    public static final String LIKED_COMMENTS = "LIKED_COMMENTS";
    public static final String LIKERS_ID_LIST = "LIKERS_ID_LIST";
    public static final String LIKED_UPDATES_LIST = "LIKED_UPDATES_LIST";
    public static final String ALL_COMMENTS = "ALL_COMMENTS";
    public static final String COMMENT_POSTER_ID = "COMMENT_POSTER_ID";
    public static final String COMMENT_DESCRIPTION = "COMMENT_DESCRIPTION";
    public static final String COMMENT_COVER_PHOTO_DOWNLOAD_URL = "COMMENT_COVER_PHOTO_DOWNLOAD_URL";
    public static final String PARENT_COMMENT_ID = "PARENT_COMMENT_ID";
    public static final String HAS_PARENT_COMMENT = "HAS_PARENT_COMMENT";
    public static final String IS_HIDDEN_BY_POSTER = "IS_HIDDEN_BY_POSTER";
    public static final String AUTHOR_ID_LIST = "AUTHOR_ID_LIST";
    public static final String UPDATE_ID_LIST = "UPDATE_ID_LIST";
    public static final String PARENT_COMMENT_ID_LIST = "PARENT_COMMENT_ID_LIST";
    public static final String COMMENT_CONTRIBUTORS_ID_LIST = "COMMENT_CONTRIBUTORS_ID_LIST";
    public static final String OTHER_UPDATES_COMMENT = "OTHER_UPDATES_COMMENT";
    public static final String IS_VIEW_SINGLE_COMMENT_REPLY = "IS_VIEW_SINGLE_COMMENT_REPLY";
    public static final String IS_VIEW_ALL_COMMENTS_FOR_SINGLE_UPDATE = "IS_VIEW_ALL_COMMENTS_FOR_SINGLE_UPDATE";
    public static final String IS_FROM_UPDATE_CONTEXT = "IS_FROM_UPDATE_CONTEXT";




    public static int ALL_JOB_NEW_ORDER_COUNT = 0;


    public static  ExoPlayer activeExoPlayer;

    public static String getBusinessAdminId(){

        return BUSINESS_ADMIN_ID ;
    }

    public static String getCurrentBusinessId(){

        return businessId ;
    }


    public static  void setCurrentBusinessId(String businessId){

        GlobalValue.businessId  = businessId ;
    }



    /**
     * <p>This method performs a check to see whether a user is logged in or not</p>
     * @return {@link boolean} denoting if a user is logged in or not
     * */
    static boolean isUserLoggedIn(){

        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    /**
     * This method sets the current user's ID
     * @param currentUserId  This parameter will be initialized from the {@link FirebaseAuth#getUid()}
     *
     *<p>It has to be first invoked from the {@link SignInActivity} context</p>
     * */
    static void setCurrentUserId(@NonNull String currentUserId){
        GlobalValue.CURRENT_USER_ID = currentUserId;
    }
    /**
     * This returns the unique ID of the current user
     *
     * @return {@link String} The unique ID of the current user
     * */
    public static String getCurrentUserId(){
        if(GlobalValue.CURRENT_USER_ID == null && FirebaseAuth.getInstance().getCurrentUser() != null){
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return GlobalValue.CURRENT_USER_ID+"";
    }
//boolean isAppVersionUpdateToDate(Context context){
//        
//
//}
    /**
     * This method sets the token of the current user
     * <p>This token is initialized from {@link com.google.firebase.auth.FirebaseUser#getIdToken(boolean)}</p>
     * This token is used for notification purpose
     * */
    static void setCurrentUserTokenId(@NonNull String currentUserTokenId){
        GlobalValue.CURRENT_USER_TOKEN_ID = currentUserTokenId;
    }

    /**
     * This returns the token of the current user
     * @return {@link String} which will be used for some operations
     * */
    static String getCurrentUserTokenId(){

        return GlobalValue.CURRENT_USER_TOKEN_ID;
    }

    /**
     * Sets the {@link FirebaseFirestore } instance
     * <p>This instance is initialized from {@link FirebaseFirestore#getInstance()}</p>
     * */
    static void setFirebaseFirestoreInstance(){
        GlobalValue.firebaseFirestoreInstance = FirebaseFirestore.getInstance();
    }
    /**
     * Returns the global instance of the {@link FirebaseFirestore} which will be
     * used to perform actions in {@link FirebaseFirestore} database
     * @return {@link FirebaseFirestore}
     * */
    public static FirebaseFirestore getFirebaseFirestoreInstance(){
        if(GlobalValue.firebaseFirestoreInstance == null){
            return FirebaseFirestore.getInstance();
        }
        return GlobalValue.firebaseFirestoreInstance;
    }

    /**
     * Sets the {@link FirebaseStorage } instance
     * <p>This instance is initialized from {@link FirebaseStorage#getInstance()}</p>
     * */
    static void setFirebaseStorageInstance(){
        GlobalValue.firebaseStorageInstance = FirebaseStorage.getInstance();
    }


    /**
     * Returns the global instance of the {@link FirebaseFirestore} which will be used to perform actions in {@link FirebaseStorage} database
     * @return {@link FirebaseStorage}
     * */
    static FirebaseStorage getFirebaseStorageInstance(){
        if(GlobalValue.firebaseStorageInstance == null){
            return FirebaseStorage.getInstance();
        }
        return GlobalValue.firebaseStorageInstance;
    }

    public static boolean isPlatformAccount(){


        return getCurrentUserId().equals(PLATFORM_ACCOUNT_ID);
    }

    public static String getPlatformId(){


        return PLATFORM_ACCOUNT_ID;
    }

    public static ArrayList<String> getJobNotificationListenersIdList(){


        return JOB_NOTIFICATION_LISTENERS_ID_ARRAY_LIST;
    }

    public static void setJobNotificationListenersIdList(ArrayList<String> listenersIdList){


         JOB_NOTIFICATION_LISTENERS_ID_ARRAY_LIST = listenersIdList;
    }
    public static ArrayList<String> getProductNotificationListenersIdList(){


        return PRODUCT_NOTIFICATION_LISTENERS_ID_ARRAY_LIST;
    }

    public static void setProductNotificationListenersIdList(ArrayList<String> listenersIdList){


        PRODUCT_NOTIFICATION_LISTENERS_ID_ARRAY_LIST = listenersIdList;
    }

//
//    public static String isOwner(){
//        return GlobalValue.isOwner;
//    }
//    public static boolean isWorker(){
//        return GlobalValue.isWorker;
//    }
//    public static boolean isCustomer(){
//        return GlobalValue.isCustomer;
//    }
    public  static  void setIsOwner(boolean isOwner){
        GlobalValue.isOwner = isOwner;
    }
    public static void setIsWorker(boolean isWorker){
        GlobalValue.isWorker = isWorker;
    }
    public static void setIsCustomer(boolean isCustomer){
        GlobalValue.isCustomer = isCustomer;
    }

    public   static   String getDate() {
        //    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, MMM d, ' ' yy h:mm a");
//    Date date = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd', 'HH:mm:ss", Locale.US);
        Date date = new Date();
        String dates = simpleDateFormat.format(date);

        //
//   String dates =  DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).format(date);
//

        return dates;
    }

    public  static boolean isBusinessOwner(){
        if(getCurrentUserId().equals(GlobalValue.getCurrentBusinessId())){
            return true;
        }

        //return true for now. but default is false
        return true;
    }

    public static void displayExoplayerVideo(Context context, StyledPlayerView styledPlayerView, ArrayList<ExoPlayer> activeExoPlayerArrayList, Uri uri) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

//                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//                TrackSelection.Factory videoTrackSelectionFactory =
//                        new AdaptiveTrackSelection.Factory(bandwidthMeter);
//                TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
//
//                SimpleExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
//                MediaSource audioSource = new ExtractorMediaSource(Uri.parse(url),
//                        new CacheDataSourceFactory(context, 100 * 1024 * 1024, 5 * 1024 * 1024), new DefaultExtractorsFactory(), null, null);
//                exoPlayer.setPlayWhenReady(true);
//                exoPlayer.prepare(audioSource);setting
//                Executor executor = Executors.newSingleThreadExecutor();
//                CronetDataSource.Factory cronetDataSourceFactory = new CronetDataSource.Factory(MainActivity.getCronetEngineInstance(),executor);
//                DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context,cronetDataSourceFactory);
//                ExoPlayer player = new ExoPlayer.Builder(context).setMediaSourceFactory(new DefaultMediaSourceFactory(dataSourceFactory)).build();
                ExoPlayer player = new ExoPlayer.Builder(context).build();
//                styledPlayerView.setResizeMode();
                styledPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                styledPlayerView.setPlayer(player);
//                styledPlayerView.setKeepContentOnPlayerReset(true);
//                styledPlayerView.set(true);

                MediaItem mediaItem = MediaItem.fromUri(uri);
                player.setMediaItem(mediaItem);
                player.addListener(new Player.Listener() {
                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
//                        ExoPlayer.release();
                        if(getActiveExoPlayer() != null) {
                            if(!getActiveExoPlayer().equals(player)){
                                getActiveExoPlayer().pause();
                            }
                        }
//                 player.addListener(new Player.Listener() {
//                     @Override
//                     public void onIsLoadingChanged(boolean isLoading) {
//                         if(isLoading){
//                             styledPlayerView.setVisibility(View.GONE);
//                         }else{
//                             styledPlayerView.setVisibility(View.VISIBLE);
//
//                         }
                        //     }
                        //});
                        setActiveExoplayer(player);

//                        player.play();

                    }
                });
                activeExoPlayerArrayList.add(player);
                player.prepare();
            }
        });



    }
    public static  ExoPlayer getActiveExoPlayer(){

        return activeExoPlayer;
    }
    public static void setActiveExoplayer(ExoPlayer activeExoPlayer){

        GlobalValue.activeExoPlayer = activeExoPlayer;
    }

    @SuppressLint("MissingPermission")
    public static boolean isConnectedOnline(Context context) {



        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if( cm.getActiveNetworkInfo() != null){
            return cm.getActiveNetworkInfo().isConnected();

        }
        return false;
    }
    public static ArrayList<WelcomeScreenItemModal> getWelcomeScreenItemsList(){
        ArrayList<WelcomeScreenItemModal> list = new ArrayList<>();
        list.add(new WelcomeScreenItemModal(R.drawable.ic_baseline_home_24, "Unlimited Access to people's products","You can access the products from different locations"));
        list.add(new WelcomeScreenItemModal(R.drawable.ic_baseline_local_grocery_store_24,"Order your interests or Apply for Jobs","You can easily order your favourite products from people in the app"));
        list.add(new WelcomeScreenItemModal(R.drawable.ic_baseline_notifications_24,"Experience of High Level Jobs, News & Updates","Experience  high level of Jobs, News and updates in kuje for free"));

        return list;
    }

    public static void showAlertMessage(String type, Context c, String title, String message){
        type = type.toLowerCase();
        LayoutInflater layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alert_view = layoutInflater.inflate(R.layout.custom_alert_box,null);
        ImageView iconView = alert_view.findViewById(R.id.icon);

        //default
        iconView.setBackgroundResource(R.drawable.ic_baseline_warning_amber_24);
        ColorStateList colorStateList = null;

        TextView titleView = alert_view.findViewById(R.id.title);
        titleView.setText(title);

        TextView messageView = alert_view.findViewById(R.id.message);
        messageView.setText(message);

        //set icon according to the type
        switch (type){
            case "success":
                iconView.setBackgroundResource(R.drawable.ic_baseline_check_circle_outline_24);
                colorStateList= new ColorStateList(
                        new int[][]{ new int[]{android.R.attr.state_pressed},
                                // pressed
                                new int[]{},
                                // default
                                new int[]{}
                        },
                        new int[]{ c.getColor(R.color.success_green),
                                // pressed
                                c.getColor(R.color.success_green),
                                // default
                                c.getColor(R.color.success_green)
                        });
                iconView.setBackgroundTintList(colorStateList);
                break;

            case "error":
                iconView.setBackgroundResource(R.drawable.ic_baseline_error_outline_24);
                colorStateList= new ColorStateList(
                        new int[][]{ new int[]{android.R.attr.state_pressed},
                                // pressed
                                new int[]{},
                                // default
                                new int[]{}
                        },
                        new int[]{ c.getColor(R.color.error_red),
                                // pressed
                                c.getColor(R.color.error_red),
                                // default
                                c.getColor(R.color.error_red)
                        });
                iconView.setBackgroundTintList(colorStateList);
                break;

            case "warning":
                iconView.setBackgroundResource(R.drawable.ic_baseline_warning_amber_24);
                colorStateList= new ColorStateList(
                        new int[][]{ new int[]{android.R.attr.state_pressed},
                                // pressed
                                new int[]{},
                                // default
                                new int[]{}
                        },
                        new int[]{ c.getColor(R.color.color_warning),
                                // pressed
                                c.getColor(R.color.color_warning),
                                // default
                                c.getColor(R.color.color_warning)
                        });
                iconView.setBackgroundTintList(colorStateList);
                break;
            default:
                break;
        }


        //show mesage
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(c);
        dialog.setView(alert_view);


        dialog.show();
    }

    public static String getRandomString(int length) {
        String[] characterArray = new String[]{
                "a", "A", "1",
                "b", "B", "2",
                "c", "C", "3",
                "d", "D", "4",
                "e", "E", "5",
                "f", "F", "6",
                "g", "G", "7",
                "h", "H", "8",
                "i", "I", "9",
                "j", "J", "0",
                "k", "K", "1",
                "l", "L", "2",
                "m", "M", "3",
                "n", "N", "4",
                "o", "O", "5",
                "p", "P", "6",
                "q", "Q", "7",
                "r", "R", "8",
                "s", "S", "9",
                "t", "T", "0",
                "u", "U", "1",
                "v", "V", "2",
                "w", "W", "3",
                "x", "X", "4",
                "y", "Y", "5",
                "z", "Z", "6"
        };

        StringBuilder randomString = new StringBuilder("AUN");

        for(int i=0; i<length; i++){

            int randomPosition = new Random().nextInt(characterArray.length -1);

            randomString.append(Array.get(characterArray, randomPosition));
        }
        return randomString + "CJD";
    }

    public static ArrayList<String> getProductCategoryList(){

        ArrayList<String> categoryList = new ArrayList<>();
        categoryList.add("ALL");
        categoryList.add("DEVICES & TECHS");
        categoryList.add("PROPERTIES");
        categoryList.add("HOMES & FURNITURE");
        categoryList.add("AUTO MOBILES");
        categoryList.add("FASHION & DESIGNING");
        categoryList.add("FOODS");
        categoryList.add("OTHERS");

        return categoryList;
    }
    public static ArrayList getCountryArrayList(@Nullable ArrayList<String> countryArrayList){
        ArrayList<String> arrayList = null;
        if(countryArrayList == null){
            arrayList = new ArrayList<>();

        }else{
            arrayList = countryArrayList;

        }

        arrayList.add("Albania");
        arrayList.add("Algeria");
        arrayList.add("American Samoa");
        arrayList.add("Andorra");
        arrayList.add("Angola");
        arrayList.add("Anguilla");
        arrayList.add("Antarctica");
        arrayList.add("Antigua and Barbuda");
        arrayList.add("Argentina");
        arrayList.add("Armenia");
        arrayList.add("Aruba");
        arrayList.add("Australia");
        arrayList.add("Austria");
        arrayList.add("Azerbaijan");
        arrayList.add("Bahamas");
        arrayList.add("Bahrain");
        arrayList.add("Bangladesh");
        arrayList.add("Barbados");
        arrayList.add("Belgium");
        arrayList.add("Belize");
        arrayList.add("Benin");
        arrayList.add("Bermuda");
        arrayList.add("Bhutan");
        arrayList.add("Bolivia");
        arrayList.add("Bonaire, Sint Eustatius and Saba");
        arrayList.add("Bosnia and Herzegovina");
        arrayList.add("Botswana");
        arrayList.add("Bouvet Island");
        arrayList.add("Brazil");
        arrayList.add("British Indian Ocean Territory");
        arrayList.add("British Virgin Islands");
        arrayList.add("Brunei Darussalam");
        arrayList.add("Bulgaria");
        arrayList.add("Burkina Faso");
        arrayList.add("Burundi");
        arrayList.add("Cambodia");
        arrayList.add("Cameroon");
        arrayList.add("Canada");
        arrayList.add("Cape Verde");
        arrayList.add("Cayman Islands");
        arrayList.add("Central African Republic");
        arrayList.add("Chad");
        arrayList.add("Chile");
        arrayList.add("China");
        arrayList.add("Christmas Island");
        arrayList.add("Cocos (Keeling) Islands");
        arrayList.add("Colombia");
        arrayList.add("Comoros");
        arrayList.add("Congo");
        arrayList.add("Cook Islands");
        arrayList.add("Costa Rica");
        arrayList.add("Cote d'Ivoire");
        arrayList.add("Croatia");
        arrayList.add("Curacao");
        arrayList.add("Cyprus");
        arrayList.add("Czech Republic");
        arrayList.add("Denmark");
        arrayList.add("Djibouti");
        arrayList.add("Dominica");
        arrayList.add("Dominican Republic");
        arrayList.add("Ecuador");
        arrayList.add("Egypt");
        arrayList.add("El Salvador");
        arrayList.add("Equatorial Guinea");
        arrayList.add("Eritrea");
        arrayList.add("Estonia");
        arrayList.add("Ethiopia");
        arrayList.add("Falkland Islands");
        arrayList.add("Faroe Islands");
        arrayList.add("Fiji");
        arrayList.add("Finland");
        arrayList.add("France");
        arrayList.add("French Guiana");
        arrayList.add("French Polynesia");
        arrayList.add("French Southern and Antarctic Lands");
        arrayList.add("Gabon");
        arrayList.add("Gambia");
        arrayList.add("Georgia");
        arrayList.add("Germany");
        arrayList.add("Ghana");
        arrayList.add("Gibraltar");
        arrayList.add("Greece");
        arrayList.add("Greenland");
        arrayList.add("Grenada");
        arrayList.add("Guadeloupe");
        arrayList.add("Guam");
        arrayList.add("Guatemala");
        arrayList.add("Guernsey");
        arrayList.add("Guinea");
        arrayList.add("Guinea-Bissau");
        arrayList.add("Guyana");
        arrayList.add("Haiti");
        arrayList.add("Heard Island and McDonald Islands");
        arrayList.add("Holy See");
        arrayList.add("Honduras");
        arrayList.add("Hong Kong");
        arrayList.add("Hungary");
        arrayList.add("Iceland");
        arrayList.add("Indonesia");
        arrayList.add("Ireland");
        arrayList.add("Isle of Man");
        arrayList.add("Israel");
        arrayList.add("Italy");
        arrayList.add("Jamaica");
        arrayList.add("Japan");
        arrayList.add("Jordan");
        arrayList.add("Kazakhstan");
        arrayList.add("Kenya");
        arrayList.add("Kiribati");
        arrayList.add("Kuwait");
        arrayList.add("Kyrgyzstan");
        arrayList.add("Laos");
        arrayList.add("Latvia");
        arrayList.add("Lebanon");
        arrayList.add("Lesotho");
        arrayList.add("Liechtenstein");
        arrayList.add("Lithuania");
        arrayList.add("Luxembourg");
        arrayList.add("Macao");
        arrayList.add("Macedonia");
        arrayList.add("Madagascar");
        arrayList.add("Malawi");
        arrayList.add("Malaysia");
        arrayList.add("Maldives");
        arrayList.add("Mali");
        arrayList.add("Malta");
        arrayList.add("Marshall Islands");
        arrayList.add("Martinique");
        arrayList.add("Mauritania");
        arrayList.add("Mauritius");
        arrayList.add("Mayotte");
        arrayList.add("Moldova");
        arrayList.add("Monaco");
        arrayList.add("Mongolia");
        arrayList.add("Montenegro");
        arrayList.add("Montserrat");
        arrayList.add("Morocco");
        arrayList.add("Mozambique");
        arrayList.add("Myanmar");
        arrayList.add("Namibia");
        arrayList.add("Nauru");
        arrayList.add("Nepal");
        arrayList.add("Netherlands");
        arrayList.add("Netherlands Antilles");
        arrayList.add("New Caledonia");
        arrayList.add("New Zealand");
        arrayList.add("Nicaragua");
        arrayList.add("Niger");
        arrayList.add("Nigeria");
        arrayList.add("Niue");
        arrayList.add("Norfolk Island");
        arrayList.add("Northern Mariana Islands");
        arrayList.add("Norway");
        arrayList.add("Oman");
        arrayList.add("Pakistan");
        arrayList.add("Palau");
        arrayList.add("Palestinian Territories");
        arrayList.add("Panama");
        arrayList.add("Papua New Guinea");
        arrayList.add("Paraguay");
        arrayList.add("Peru");
        arrayList.add("Philippines");
        arrayList.add("Pitcairn");
        arrayList.add("Poland");
        arrayList.add("Portugal");
        arrayList.add("Puerto Rico");
        arrayList.add("Qatar");
        arrayList.add("Reunion");
        arrayList.add("Romania");
        arrayList.add("Rwanda");
        arrayList.add("Saint Barthelemy");
        arrayList.add("Saint Helena");
        arrayList.add("Saint Kitts and Nevis");
        arrayList.add("Saint Lucia");
        arrayList.add("Saint Martin (French part)");
        arrayList.add("Saint Pierre and Miquelon");
        arrayList.add("Saint Vincent and the Grenadines");
        arrayList.add("Samoa");
        arrayList.add("San Marino");
        arrayList.add("Sao Tome and Principe");
        arrayList.add("Saudi Arabia");
        arrayList.add("Senegal");
        arrayList.add("Serbia");
        arrayList.add("Seychelles");
        arrayList.add("Sierra Leone");
        arrayList.add("Singapore");
        arrayList.add("Sint Maarten (Dutch part)");
        arrayList.add("Slovakia");
        arrayList.add("Slovenia");
        arrayList.add("Solomon Islands");
        arrayList.add("Somalia");
        arrayList.add("South Africa");
        arrayList.add("South Korea");
        arrayList.add("Spain");
        arrayList.add("Sri Lanka");
        arrayList.add("Suriname");
        arrayList.add("Svalbard and Jan Mayen");
        arrayList.add("Swaziland");
        arrayList.add("Sweden");
        arrayList.add("Switzerland");
        arrayList.add("Taiwan");
        arrayList.add("Tajikistan");
        arrayList.add("Tanzania");
        arrayList.add("Thailand");
        arrayList.add("Timor-Leste");
        arrayList.add("Togo");
        arrayList.add("Tokelau");
        arrayList.add("Tonga");
        arrayList.add("Trinidad and Tobago");
        arrayList.add("Tunisia");
        arrayList.add("Turkey");
        arrayList.add("Turkmenistan");
        arrayList.add("Turks and Caicos Islands");
        arrayList.add("Tuvalu");
        arrayList.add("Uganda");
        arrayList.add("Ukraine");
        arrayList.add("United Arab Emirates");
        arrayList.add("United Kingdom");
        arrayList.add("United States Minor Outlying Islands");
        arrayList.add("United States Virgin Islands");
        arrayList.add("Uruguay");
        arrayList.add("Uzbekistan");
        arrayList.add("Vanuatu");
        arrayList.add("Venezuela");
        arrayList.add("Vietnam");
        arrayList.add("Wallis and Futuna");
        arrayList.add("Western Sahara");
        arrayList.add("Zambia");
        arrayList.add("Zimbabwe");

        return arrayList;
    }

    public static  void checkIfDocumentExists(DocumentReference documentReference, OnDocumentExistStatusCallback onDocumentExistStatusCallback){
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        onDocumentExistStatusCallback.onExist();
                    }else if (!documentSnapshot.exists()){
                        onDocumentExistStatusCallback.onNotExist();
                    }
                }
                else{
                    onDocumentExistStatusCallback.onFailed(task.getException().getMessage());
                }
            }
        });
    }

    public static void deleteImage(FirebaseStorage firebaseStorage, StorageReference appStorageReference, String imageStorageReference, OnDeleteImageListener onDeleteImageListener) {

        appStorageReference.child(imageStorageReference).delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onDeleteImageListener.onDeleteImageFailed(e);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                onDeleteImageListener.onDeleteImageSuccess(unused);
            }
        });
    }

    public static Uri getImageUri(Context context, Bitmap bitmap,int percent) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, percent, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "AAG", "AAG Homes");
        return Uri.parse(path);
    }

    /**
     * A callback method that tells when a user has either succeeded or failed in signing in to the platform
     * <p>{@link SignInListener#onSuccess(String, String)} - this method is triggered when a user signs in successfully  </p>
     * <p>{@link SignInListener#onFailed(String)} - this method is triggered when a user signs in process fails  </p>
     * <p>{@link SignInListener#onEmptyInput(boolean, boolean)} - this method is triggered when a user has an empty input  </p>
     * */
    interface SignInListener{
        void onSuccess(String email, String password);
        void onFailed(String errorMessage);
        void onEmptyInput(boolean isEmailEmpty, boolean isPasswordEmpty);
    }

    /**
     * A callback method that tells when a user has either succeeded or failed in signing in to the platform
     * <p>{@link SignUpListener#onSuccess(String, String)} - this method is triggered when a user signs in successfully  </p>
     * <p>{@link SignUpListener#onFailed(String)} - this method is triggered when a user signs in process fails  </p>
     * <p>{@link SignUpListener#onEmptyInput(boolean, boolean)} - this method is triggered when a user has an empty input  </p>
     * */
    interface SignUpListener{
        void onSuccess(String email, String password);
        void onFailed(String errorMessage);
        void onEmptyInput(boolean isEmailEmpty, boolean isPasswordEmpty);
    }

    /**
     * Signs a user in to the platform
     * @param email the email of the user
     * @param password the password of the user
     * @param signInListener a callback that tells when the sign in fails or succeeds
     * */
    public  static void signInUserWithEmailAndPassword(Context context,@NonNull String email, @NonNull String password,SignInListener signInListener){
        if(email != null && password != null){
            if(!email.isEmpty() && !password.isEmpty()){
                String  trimmedEmail = email.trim();
                String trimmedPassword = password.trim();
                FirebaseAuth firebaseAuth =    FirebaseAuth.getInstance();

                firebaseAuth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                signInListener.onFailed(e.getMessage());
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                signInListener.onSuccess(trimmedEmail, trimmedPassword);
                            }
                        });
            }else{
                signInListener.onEmptyInput(email.isEmpty(),password.isEmpty());
            }
        }else{
            signInListener.onEmptyInput(email == null,password == null);

        }
    }

    /**
     * Creates a user account
     * @param email the email of the user
     * @param password the password of the user
     * @param signUpListener a callback that tells when the sign un fails or succeeds
     * */
    public static void signUpUserWithEmailAndPassword(Context context,@NonNull String email,@NonNull  String password,SignUpListener signUpListener){
        if(email != null && password != null){
            if(!email.isEmpty() && !password.isEmpty()){
                String  trimmedEmail = email.trim();
                String trimmedPassword = password.trim();
                FirebaseAuth firebaseAuth =    FirebaseAuth.getInstance();

                firebaseAuth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                signUpListener.onFailed(e.getMessage());
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                signUpListener.onSuccess(trimmedEmail, trimmedPassword);
                            }
                        });
            }else{
                signUpListener.onEmptyInput(email.isEmpty(),password.isEmpty());
            }
        }else{
            signUpListener.onEmptyInput(email == null,password == null);

        }
    }


    public static void deleteDocument(DocumentReference documentReference, OnDeleteDocumentListener onDeleteDocumentListener) {
        documentReference.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onDeleteDocumentListener.onDeleteDocumentFailed(e);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                onDeleteDocumentListener.onDeleteDocumentSuccess(unused);
            }
        });
    }

    /**
     * This method generates keywords which is used for performing search operation in the database, it trims the given {@link String}
     * into words which enables us query and match each word for similarity in the database.
     * @param itemName the {@link String} to be trimmed
     * @return {@link ArrayList} the list that contains the keywords
     * */
    public static ArrayList<String> generateSearchVerbatimKeyWords(@NonNull String itemName){
        ArrayList<String> searchVerbatimKeywordsArrayList = new ArrayList<>();

        if(itemName != null && !itemName.isEmpty()) {
            itemName = itemName.toLowerCase();
            int itemLength = itemName.length();
            searchVerbatimKeywordsArrayList.add(itemName);
            searchVerbatimKeywordsArrayList.addAll(Arrays.asList(itemName.split(" ")));

        }else{
            searchVerbatimKeywordsArrayList = new ArrayList<>();
        }

        return searchVerbatimKeywordsArrayList;
    }

    public static Snackbar createSnackBar(Context context , View view, String text, int lengthPeriod){
        Snackbar snackBar = Snackbar.make(view,text,lengthPeriod);
        snackBar.setAction("Hide", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });
        snackBar.show();

        return snackBar;
    }

    /**
     * This method generates keywords which is used for performing search operation in the database, it trims the given {@link String}
     * into {@link Character} which enables us query and match each word for similarity in the database.
     * @param itemName the {@link String} to be trimmed
     * @return {@link ArrayList} the list that contains the keywords
     * */

    public static ArrayList<String> generateSearchAnyMatchKeyWords(@NonNull String itemName) {
        ArrayList<String> searchAnyMatchKeywordsArrayList = new ArrayList<>();

        if (itemName != null && !itemName.isEmpty()) {
            itemName = itemName.toLowerCase();
            int itemLength = itemName.length();

            if (itemLength != 0) {
                for (int i = 0; i < itemLength; i++) {
                    searchAnyMatchKeywordsArrayList.add(itemName.substring(i));


                    for (int j = itemLength - 1; j > i; j--) {

                        searchAnyMatchKeywordsArrayList.add(itemName.substring(i, j));


                    }


                }

                for (int i = 0; i < itemLength; i++) {
                    for (int k = i + 1; k < itemLength; k++) {
                        if (!searchAnyMatchKeywordsArrayList.contains(itemName.charAt(i) + "" + itemName.charAt(k))) {

                            searchAnyMatchKeywordsArrayList.add(itemName.charAt(i) + "" + itemName.charAt(k));

                        }
                    }

                }
            }
        }
        return searchAnyMatchKeywordsArrayList;
    }

    public static Menu createPopUpMenu(Context context, int menuRes, View anchorView, OnMenuItemClickListener onMenuItemClickListener) {


        PopupMenu popupMenu = new PopupMenu(context, anchorView);
        popupMenu.getMenuInflater().inflate(menuRes, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuItemClickListener.onMenuItemClicked(item);
                return false;

            }
        });
        popupMenu.show();

            return popupMenu.getMenu();
    }
    public static Menu createPopUpMenu(Context context, int menuRes, View anchorView,  PopupMenu popupMenu,boolean show, OnMenuItemClickListener onMenuItemClickListener) {

if(popupMenu==null) {
    popupMenu = new PopupMenu(context, anchorView);
}
        popupMenu.getMenuInflater().inflate(menuRes, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuItemClickListener.onMenuItemClicked(item);
                return false;

            }
        });
        if(show) {
            popupMenu.show();
        }

            return popupMenu.getMenu();
    }
    public  static ArrayList<String> generateSearchKeyWords(String itemName, int requiredLength){

        itemName = itemName.toLowerCase();
        int itemLength = itemName.length();
        String approvedItemName = itemName;
        if(itemLength >65){
            approvedItemName = itemName.substring(0,65);
        }
        int approvedItemNameLength = approvedItemName.length();
        if(requiredLength < 65 && !(requiredLength > itemLength)){
            approvedItemNameLength = requiredLength;
        }

// if(isLessImportant && requiredLength < 10){
//     approvedItemNameLength = requiredLength;
// }else if(isLessImportant && requiredLength > 10){
//
// }
        ArrayList<String> searchKeywordsArrayList = new ArrayList<>();
        if(approvedItemNameLength != 0) {
            for(int i=0; i<approvedItemNameLength; i++) {
                searchKeywordsArrayList.add(approvedItemName.substring(i));


                for(int j = approvedItemNameLength-1; j>i; j--){

                    searchKeywordsArrayList.add(approvedItemName.substring(i, j));


                }


            }

//         char[] approvedChar = approvedItemName.toCharArray();
//         int charLength = approvedChar.length ;
            for(int i=0; i<approvedItemNameLength; i++){
                for(int k = i+1; k < approvedItemNameLength ; k++) {
                    if(!searchKeywordsArrayList.contains(approvedItemName.charAt(i) + "" + approvedItemName.charAt(k))) {

                        searchKeywordsArrayList.add(approvedItemName.charAt(i) + "" + approvedItemName.charAt(k));

                    }
                }

            }
        }

        return searchKeywordsArrayList;
    }
    public static ArrayList<String> generateHashTagKeyWords(String textToMatch){
        ArrayList<String> textMatchedArrayList = new ArrayList<>();
        textToMatch = textToMatch.toLowerCase();
        textToMatch = textToMatch.replace("\n"," ");
        if(textToMatch!= null){
            String[] textSplittedArray = textToMatch.split(" ");
            for(String textSplitted: textSplittedArray) {
//             if (textToMatch.matches("[#]+[A-Za-z0-9-_]\\b")) {
                if (textSplitted.startsWith("#")) {
                    if (!textMatchedArrayList.contains(textSplitted)) {
                        String finalWord = textSplitted;
                        textMatchedArrayList.add(finalWord);
                    }
                }

            }

        }
        return textMatchedArrayList;
    }
    public static Intent getHostActivityIntent(Context context, @Nullable Intent intent, String fragmentOpenType, String userId){
        if(intent == null){
            intent = new Intent(context,HostActivity.class);
        }

        intent.putExtra(FRAGMENT_TYPE,fragmentOpenType);
        intent.putExtra(USER_ID,userId);

        return  intent;
    }

    public static void incrementProductViews(String productId){
        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();
        DocumentReference productDocumentReference =  getFirebaseFirestoreInstance().collection(ALL_PRODUCTS).document(productId);
        HashMap<String,Object> incrementDetails = new HashMap<>();
        incrementDetails.put(TOTAL_NUMBER_OF_VIEWS, FieldValue.increment(1L));
        incrementDetails.put(GlobalValue.IS_NEW, false);
        writeBatch.update(productDocumentReference,incrementDetails);
//
//         DocumentReference viewDocumentReference =  getFirebaseFirestoreInstance().collection(ALL_PRODUCTS).document(productId).collection(ALL_VIEWS).document(getCurrentUserId());
//        HashMap<String,Object> viewDetails = new HashMap<>();
//        viewDetails.put(TOTAL_NUMBER_OF_VIEWS, FieldValue.increment(1L));
//        writeBatch.set(viewDocumentReference,viewDetails, SetOptions.merge());

        writeBatch.commit();

    }
    public static void incrementJobViews(String jobId){
        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();
        DocumentReference productDocumentReference =  getFirebaseFirestoreInstance().collection(ALL_JOBS).document(jobId);
        HashMap<String,Object> incrementDetails = new HashMap<>();
        incrementDetails.put(TOTAL_NUMBER_OF_VIEWS, FieldValue.increment(1L));
        incrementDetails.put(GlobalValue.IS_NEW, false);
        writeBatch.update(productDocumentReference,incrementDetails);
//
//         DocumentReference viewDocumentReference =  getFirebaseFirestoreInstance().collection(ALL_PRODUCTS).document(productId).collection(ALL_VIEWS).document(getCurrentUserId());
//        HashMap<String,Object> viewDetails = new HashMap<>();
//        viewDetails.put(TOTAL_NUMBER_OF_VIEWS, FieldValue.increment(1L));
//        writeBatch.set(viewDocumentReference,viewDetails, SetOptions.merge());

        writeBatch.commit();

    }
    public static void incrementUpdateViews(String updateId){
        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();
        DocumentReference productDocumentReference =  getFirebaseFirestoreInstance().collection(PLATFORM_UPDATES).document(updateId);
        HashMap<String,Object> incrementDetails = new HashMap<>();
        incrementDetails.put(TOTAL_NUMBER_OF_VIEWS, FieldValue.increment(1L));
        incrementDetails.put(GlobalValue.IS_NEW, false);
        writeBatch.update(productDocumentReference,incrementDetails);

        DocumentReference viewDocumentReference =  getFirebaseFirestoreInstance().collection(PLATFORM_UPDATES).document(updateId).collection(ALL_VIEWS).document(getCurrentUserId());
        HashMap<String,Object> viewDetails = new HashMap<>();
        viewDetails.put(TOTAL_NUMBER_OF_VIEWS, FieldValue.increment(1L));
        writeBatch.set(viewDocumentReference,viewDetails, SetOptions.merge());

        writeBatch.commit();

    }

    public static void createNote(String noteIdEdit,String noteTitle, String noteBody,boolean isCreateNewNote,ActionCallback actionCallback){
        String noteId = getRandomString(100);

        ArrayList<String> searchAnyMatchKeywordArrayList = new ArrayList<>();
        ArrayList<String> searchVerbatimKeywordArrayList = new ArrayList<>();
        searchAnyMatchKeywordArrayList = GlobalValue.generateSearchAnyMatchKeyWords(noteTitle);
        searchVerbatimKeywordArrayList = GlobalValue.generateSearchVerbatimKeyWords(noteTitle);

        HashMap<String,Object>noteDetails = new HashMap<>();
        noteDetails.put(NOTE_TITLE,noteTitle);
        noteDetails.put(NOTE_BODY,noteBody);
        noteDetails.put(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchAnyMatchKeywordArrayList);
        noteDetails.put(GlobalValue.SEARCH_VERBATIM_KEYWORD, searchVerbatimKeywordArrayList);


        if(isCreateNewNote){
            noteDetails.put(DATE_ADDED_TIME_STAMP,FieldValue.serverTimestamp());
            noteDetails.put(DATE_EDITED_TIME_STAMP,FieldValue.serverTimestamp());

        }else{
            noteDetails.put(DATE_EDITED_TIME_STAMP,FieldValue.serverTimestamp());
            noteId = noteIdEdit;

        }
//
        noteDetails.put(NOTE_ID,noteId);
        getFirebaseFirestoreInstance().collection(PLATFORM_NOTES).document(noteId)
                .set(noteDetails,SetOptions.merge())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    actionCallback.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    actionCallback.onSuccess();
                    }
                });
    }
    public static String createSalesRecord(String recordIdEdit,String recordTitle, String recordCaption,boolean isCreateNewRecord,ActionCallback actionCallback){
        String recordId = getRandomString(100);

        ArrayList<String> searchAnyMatchKeywordArrayList = new ArrayList<>();
        ArrayList<String> searchVerbatimKeywordArrayList = new ArrayList<>();
        searchAnyMatchKeywordArrayList = GlobalValue.generateSearchAnyMatchKeyWords(recordTitle);
        searchVerbatimKeywordArrayList = GlobalValue.generateSearchVerbatimKeyWords(recordTitle);

        HashMap<String,Object>recordDetails = new HashMap<>();
        recordDetails.put(RECORD_TITLE,recordTitle);
        recordDetails.put(RECORD_CAPTION,recordCaption);
        recordDetails.put(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchAnyMatchKeywordArrayList);
        recordDetails.put(GlobalValue.SEARCH_VERBATIM_KEYWORD, searchVerbatimKeywordArrayList);

        if(isCreateNewRecord){
            recordDetails.put(DATE_ADDED_TIME_STAMP,FieldValue.serverTimestamp());
            recordDetails.put(DATE_EDITED_TIME_STAMP,FieldValue.serverTimestamp());

        }else{
            recordDetails.put(DATE_EDITED_TIME_STAMP,FieldValue.serverTimestamp());
            recordId = recordIdEdit;

        }
        recordDetails.put(RECORD_ID,recordId);

        getFirebaseFirestoreInstance().collection(PLATFORM_SALES_RECORD).document(recordId)
                .set(recordDetails,SetOptions.merge())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    actionCallback.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    actionCallback.onSuccess();
                    }
                });
        return recordId;
    }

    public static void hideKeyboard(Context context, Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        inputMethodManager.toggleSoftInput(1,0);

//        if(view != null){
//            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
//
//        }else{
//            if(new View(activity).getWindowToken() != null) {
//                inputMethodManager.hideSoftInputFromWindow(new View(activity).getWindowToken(), 0);
//            }
//
//        }

    }
    public static void hideKeyboard(Context context){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(new View(context).getWindowToken(),0);
        inputMethodManager.toggleSoftInput(0,0);

    }
    public static void openKeyboard(Context context, EditText editText){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(1,0);
        editText.requestFocus();
    }

    public static  String getFormattedDate(String dateString){

        if(dateString != null ){

            if(!dateString.equals("null") ) {
                if (dateString.length() == 20) {

                    if (!dateString.equals("") || !dateString.isEmpty()) {


                        String currentDate = GlobalValue.getDate();

                        int actionMinute = Integer.parseInt(dateString.substring(15, 17));
                        int actionHour = Integer.parseInt(dateString.substring(12, 14));
                        int actionDay = Integer.parseInt(dateString.substring(8, 10));
                        int actionMonth = Integer.parseInt(dateString.substring(5, 7));
                        int actionYear = Integer.parseInt(dateString.substring(0, 4));

                        int currentMinute = Integer.parseInt(currentDate.substring(15, 17));
                        int currentHour = Integer.parseInt(currentDate.substring(12, 14));
                        int currentDay = Integer.parseInt(currentDate.substring(8, 10));
                        int currentMonth = Integer.parseInt(currentDate.substring(5, 7));
                        int currentYear = Integer.parseInt(currentDate.substring(0, 4));

                        if (actionYear == currentYear && actionMonth == currentMonth && actionDay == currentDay && actionHour == currentHour && actionMinute == currentMinute) {

                            return "now";
                        } else if (actionYear == currentYear && actionMonth == currentMonth && actionDay == currentDay && actionHour == currentHour) {

                            return currentMinute - actionMinute + "m";
                        } else if (actionYear == currentYear && actionMonth == currentMonth && actionDay == currentDay && actionHour < currentHour) {

                            return (currentHour - actionHour) + "h ";
                        } else if (actionYear == currentYear && actionMonth == currentMonth && actionDay < currentDay) {


                            if (currentDay - actionDay == 1) {

                                return "Yesterday";
                            } else if (currentDay - actionDay == 2) {

                                return " 2 days ago";
                            } else if (currentDay - actionDay == 3) {

                                return " 3 days ago";
                            } else if (currentDay - actionDay == 4) {

                                return " 4 days ago";
                            } else if (currentDay - actionDay == 5) {

                                return " 5 days ago";
                            } else if (currentDay - actionDay == 6) {

                                return " 6 days ago";
                            }

//                else if (currentDay - actionDay <= 7) {
//
//                    return "This week";
//                } else if (currentDay - actionDay < 14) {
//
//                    return "Last week";
//                } else if ((currentDay - actionDay >= 14) && currentDay - actionDay < 21) {
//
//                    return "2 weeks ago";
//                } else if ((currentDay - actionDay >= 21) && currentDay - actionDay < 30) {
//
//                    return "3 weeks ago";
//                }


                            return dateString;
                        }

//            else if (actionYear == currentYear && actionMonth < currentMonth) {
//
//                if ( currentMonth - actionMonth < 12) {
//                    return actionMonth + " Month ago";
//                }
//                return "This year";
//            } else {
//                return actionYear - currentYear + " Year ago";
//            }


                    }

                }
            }
        }
        return dateString;
    }

    static void saveImageToLocalStorage(Context context,Bitmap bitmap, String fileName,ActionCallback actionCallback){


        boolean isAvailable=false;
        boolean isWritable=false;
        boolean isReadable=false;
        String state= Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {

            isAvailable = true;
            isWritable = true;
            isReadable = true;

            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                File folder = context.getFilesDir();
//                folder.mkdirs();

//                File fileFolder = new File(folder, "Obidient Era");
            File file2 = new File(folder, fileName);
            if(android.os.Build.VERSION.SDK_INT >=29) {

                try {
                    ByteArrayOutputStream BAOS = new ByteArrayOutputStream();

                    FileOutputStream fileOutputStream = new FileOutputStream(file2);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                    contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,"Govance Inc Media"+ fileName);
                    contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());


                    contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures");
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, true);

                    Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    OutputStream outputStream = context.getContentResolver().openOutputStream(uri);

                    if (uri != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, false);
                        context.getContentResolver().update(uri, contentValues, null, null);
                        actionCallback.onSuccess();
                        Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show();


                    }

// contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/png");
//                    contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,"OBI");
//                    contentValues.put(MediaStore.Images.Media.DATE_TAKEN,System.currentTimeMillis());
//                    contentValues.put(MediaStore.Images.Media.DATE_ADDED,System.currentTimeMillis());
//                    contentValues.put(MediaStore.Images.Media.DATA,folder.getAbsolutePath());
//                    context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);


                    //                     fileOutputStream.write(BAOS.toByteArray());


                    //                     Intent intent=  new Intent("android.intent.action.ACTION_MEDIA_SCANNER_SCAN_FILE");
                    //  Uri uri= Uri.fromFile(file2);
                    // intent.setData(uri);
//                    Toast.makeText(context,"file saving...", Toast.LENGTH_LONG).show();
//                    MediaScannerConnection.scanFile(context, new String[]{file2.toString()}, new String[]{file2.getName()}, new MediaScannerConnection.OnScanCompletedListener() {
//                        @Override
//                        public void onScanCompleted(String path, Uri uri) {
//                           new Handler(Looper.getMainLooper()).post(new Runnable() {
//                               @Override
//                               public void run() {
//                                   Toast.makeText(context, "Image path saved : " + path, Toast.LENGTH_LONG).show();
//                                   actionCallback.onActionSuccess();
//
//                               }
//                           });
//                           }
//                    });
//                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file2)));

                    outputStream.flush();
                    outputStream.close();
                    //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED)Uri.parse("file://"+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));

                } catch (IOException ioe) {
                    actionCallback.onFailed(ioe.getMessage());
                }
            }
            else{
                try {
//                        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();

                    folder = Environment.getExternalStorageDirectory();
//                File folder = context.getFilesDir();
//                folder.mkdirs();

//                File fileFolder = new File(folder, "Obidient Era");
                    file2 = new File(folder,"Govance Inc Media"+ fileName);

                    FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    OutputStream outputStream = null;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                    contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                    contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                    contentValues.put(MediaStore.Images.Media.DATA,file2.getAbsolutePath());
                    Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    if (uri != null) {
                        outputStream = context.getContentResolver().openOutputStream(uri);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        actionCallback.onSuccess();
                        Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show();
                    }

// contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/png");
//                    contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,"OBI");
//                    contentValues.put(MediaStore.Images.Media.DATE_TAKEN,System.currentTimeMillis());
//                    contentValues.put(MediaStore.Images.Media.DATE_ADDED,System.currentTimeMillis());
//                    contentValues.put(MediaStore.Images.Media.DATA,folder.getAbsolutePath());
//                    context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);


                    //                     fileOutputStream.write(BAOS.toByteArray());


                    //                     Intent intent=  new Intent("android.intent.action.ACTION_MEDIA_SCANNER_SCAN_FILE");
                    //  Uri uri= Uri.fromFile(file2);
                    // intent.setData(uri);
//                    Toast.makeText(context,"file saving...", Toast.LENGTH_LONG).show();
//                    MediaScannerConnection.scanFile(context, new String[]{file2.toString()}, new String[]{file2.getName()}, new MediaScannerConnection.OnScanCompletedListener() {
//                        @Override
//                        public void onScanCompleted(String path, Uri uri) {
//                           new Handler(Looper.getMainLooper()).post(new Runnable() {
//                               @Override
//                               public void run() {
//                                   Toast.makeText(context, "Image path saved : " + path, Toast.LENGTH_LONG).show();
//                                   actionCallback.onActionSuccess();
//
//                               }
//                           });
//                           }
//                    });
//                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file2)));
                    if(outputStream  != null){
                        outputStream.flush();
                        outputStream.close();
                    }
                    //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED)Uri.parse("file://"+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));

                } catch (IOException ioe) {
                    actionCallback.onFailed(ioe.getMessage());
                }
            }
            //Toast.makeText(this, "good to go", Toast.LENGTH_SHORT).show();
        }
        else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

            isAvailable = true;
            isWritable = false;
            isReadable = true;
//                Toast.makeText(this, "good to read", Toast.LENGTH_SHORT).show();

        }
        else {
            isAvailable = false;
            isWritable = false;
            isReadable = false;
        }


    }

    static void displayPlainMessage(Context context, ViewGroup viewGroup, DocumentReference allMessagesDocumentDirectoryReference, DocumentReference messageDocumentReference, String messageId, String textMessage, String imageDownloadUrl, String videoDownloadUrl, String currentSenderId, String senderDisplayName, String senderId, String recipientId, String dateSent, boolean isTextIncluded, boolean isImageIncluded, boolean isSendImage, ImageView  sendingImageView, boolean isVideoIncluded, boolean isCurrentUserTheSender, boolean isLoadFromSnapshot, boolean isLoadFromCache, boolean hasPendingWrites, boolean isDeliveredToRecipient, boolean isSeenByRecipient, boolean isDeliveredToCurrentUser, boolean isSeenByCurrentUser, OnMessageImageUploadListener onMessageImageUploadListener){
        if(sendingImageView != null){
            sendingImageView.setDrawingCacheEnabled(true);
        }
        String[] messageIdArray = new String[1];
        messageIdArray[0] = messageId;

        if(isSendImage){
            if(messageId == null){
                messageId = getRandomString(60);
                messageIdArray[0] = messageId;
                if(messageDocumentReference ==  null){
                    messageDocumentReference = allMessagesDocumentDirectoryReference.collection("MESSAGES").document(messageId);
                }
            }
        }

        if(messageDocumentReference ==  null){
            messageDocumentReference = allMessagesDocumentDirectoryReference.collection("MESSAGES").document(messageId);
        }

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View messageView =  layoutInflater.inflate(R.layout.plain_message_view_holder_layout,viewGroup,false);
        CardView bodyMessageMainCardView = messageView.findViewById(R.id.bodyMessageMainCardViewId);
        LinearLayout mainLinearLayout = messageView.findViewById(R.id.mainLinearLayoutId);
        LinearLayout bodyMessageMainLinearLayout = messageView.findViewById(R.id.bodyMessageMainLinearLayoutId);
        View leftDummyView = messageView.findViewById(R.id.leftDummyViewId);
        View rightDummyView = messageView.findViewById(R.id.rightDummyViewId);

        TextView messageSenderDisplayNameTextView = messageView.findViewById(R.id.messageSenderDisplayNameTextViewId);
        TextView textMessageTextView = messageView.findViewById(R.id.textMessageTextViewId);
        TextView dateSentTextView = messageView.findViewById(R.id.dateSentTextViewId);

        ImageView messageStateIndicatorImageView = messageView.findViewById(R.id.messageStateIndicatorImageViewId);

        TextView recipientIdHolderDummyTextView = messageView.findViewById(R.id.recipientIdHolderDummyTextViewId);
        TextView senderIdHolderDummyTextView = messageView.findViewById(R.id.senderIdHolderDummyTextViewId);
        TextView messageIdHolderDummyTextView = messageView.findViewById(R.id.messageIdHolderDummyTextViewId);


        RelativeLayout messageImageViewRelativeLayout = messageView.findViewById(R.id.messageImageViewRelativeLayoutId);
        ImageView imageMessageImageView = messageView.findViewById(R.id.imageMessageImageViewId);
        StyledPlayerView videoMessageStyledPlayerView = messageView.findViewById(R.id.videoMessageStyledPlayerViewId);



        FloatingActionButton startUploadImageView = messageView.findViewById(R.id.startUploadButtonId);
        FloatingActionButton successImageView = messageView.findViewById(R.id.successImageViewId);

        FloatingActionButton pauseUploadImageView = messageView.findViewById(R.id.pauseUploadButtonId);

        FloatingActionButton resumeUploadImageView = messageView.findViewById(R.id.resumeUploadButtonId);

        ProgressBar progressBar = messageView.findViewById(R.id.progressIndicatorId);

        messageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(senderId.equals(currentSenderId)) {
                    createPopUpMenu(context, R.menu.message_menu_action, messageStateIndicatorImageView, new OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClicked(MenuItem item) {

                            if(!textMessage.equals("-MESSAGE DELETED-")) {
                                if (isConnectedOnline(context)) {
                                    if (item.getItemId() == R.id.deleteMessageId) {
                                        deleteMessage(allMessagesDocumentDirectoryReference, messageIdHolderDummyTextView.getText().toString(), isTextIncluded, isImageIncluded, isVideoIncluded);
                                        textMessageTextView.setText("-MESSAGE DELETED-");
                                        messageImageViewRelativeLayout.setVisibility(View.GONE);
                                        videoMessageStyledPlayerView.setVisibility(View.GONE);
                                        messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_sending_12);

//                viewGroup.removeView(messageView);
//              viewGroup.indexOfChild(messageView);
                                    }
                                } else {
                                    Toast.makeText(context, "No network, please try and connect!", Toast.LENGTH_SHORT).show();
                                }
                            }
//            else{
//    Toast.makeText(context, "No network, please try and connect!", Toast.LENGTH_SHORT).show();
//}


                            return true;
                        }
                    });

                }
                return true;
            }
        });

        messageSenderDisplayNameTextView.setText(senderDisplayName);
        dateSentTextView.setText(getFormattedDate(dateSent));
        textMessageTextView.setText(textMessage);
        imageMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewImagePreview(context,imageMessageImageView, imageDownloadUrl);
            }
        });

        imageMessageImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                createPopUpMenu(context, R.menu.image_message_menu_action, imageMessageImageView, new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClicked(MenuItem item) {

                        if (item.getItemId() == R.id.saveImageId) {
                            saveImageToLocalStorage(context, imageMessageImageView.getDrawingCache(true), textMessage, new ActionCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailed(String message) {

                                }
                            });

                        }


                        return true;
                    }
                });


                return true;
            }
        });

        if(isTextIncluded){
            textMessageTextView.setVisibility(View.VISIBLE);
        }

        if(isImageIncluded){
            messageImageViewRelativeLayout.setVisibility(View.VISIBLE);
            imageMessageImageView.setVisibility(View.VISIBLE);
            if(imageDownloadUrl != null  && !isSendImage) {
//                Glide.with(context).load(imageDownloadUrl).into(imageMessageImageView);

                Picasso.get().load(imageDownloadUrl).error(R.drawable.ic_baseline_image_24).into(imageMessageImageView);
            }
        }
        if(isSendImage){
            if(sendingImageView != null) {
                sendingImageView.setDrawingCacheEnabled(true);
                sendingImageView.buildDrawingCache();
                imageMessageImageView.setImageBitmap(((BitmapDrawable) sendingImageView.getDrawable()).getBitmap());
            }else {
                if (imageDownloadUrl != null) {
//                Glide.with(context).load(imageDownloadUrl).into(imageMessageImageView);
//                    imageMessageImageView.setImageURI(Uri.parse(imageDownloadUrl));
                }
            }
            String imageId = getRandomString(60);

            messageImageViewRelativeLayout.setVisibility(View.VISIBLE);
            imageMessageImageView.setVisibility(View.VISIBLE);
            pauseUploadImageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
//            if(sendingImageView != null) {
//                imageMessageImageView.setImageBitmap(sendingImageView.getDrawingCache());
//            }

            StorageReference sentMessageImageStorageReference = FirebaseStorage.getInstance().getReference().child("ALL_MESSAGES/"+currentSenderId+"/MESSAGE_IMAGES/"+imageId+".PNG");
            imageMessageImageView.setDrawingCacheEnabled(true);

            imageMessageImageView.setDrawingCacheEnabled(true);
            imageMessageImageView.buildDrawingCache();

            Bitmap messageImageBitmap = null;
            if(sendingImageView != null) {
                messageImageBitmap = ((BitmapDrawable) sendingImageView.getDrawable()).getBitmap();
            }else{
                messageImageBitmap = ((BitmapDrawable) imageMessageImageView.getDrawable()).getBitmap();

            }


//            Bitmap messageImageBitmap = imageMessageImageView.getDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            messageImageBitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();

            UploadTask messageImageUploadTask = sentMessageImageStorageReference.putBytes(bytes);

            pauseUploadImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    messageImageUploadTask.pause();
                    progressBar.setVisibility(View.GONE);
                    pauseUploadImageView.setVisibility(View.GONE);
                    resumeUploadImageView.setVisibility(View.VISIBLE);
                }
            });

            resumeUploadImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    messageImageUploadTask.resume();
                    progressBar.setVisibility(View.VISIBLE);
                    resumeUploadImageView.setVisibility(View.GONE);
                    pauseUploadImageView.setVisibility(View.VISIBLE);
                }
            });


            messageImageUploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "failed to upload", Toast.LENGTH_SHORT).show();
                            startUploadImageView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            pauseUploadImageView.setVisibility(View.GONE);
                            resumeUploadImageView.setVisibility(View.GONE);

                        }
                    });

                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                            deleteVideoButton.setVisibility(View.GONE);
                            double uploadSize = snapshot.getTotalByteCount();
                            double uploadedSize = snapshot.getBytesTransferred();
                            double remainingSize = uploadSize - uploadedSize;
                            int uploadProgress = (int) ((100 * uploadedSize) / uploadSize);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(uploadProgress);

                                }
                            });


                            // Toast.makeText(context, "progressing..." + uploadProgress, Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Toast.makeText(context, "image uploaded", Toast.LENGTH_SHORT).show();

                            messageImageUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!(task.isSuccessful())) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, ""+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }

                                    return sentMessageImageStorageReference.getDownloadUrl();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, "failed to upload", Toast.LENGTH_SHORT).show();
                                                    startUploadImageView.setVisibility(View.VISIBLE);
                                                    progressBar.setVisibility(View.GONE);
                                                    pauseUploadImageView.setVisibility(View.GONE);
                                                    resumeUploadImageView.setVisibility(View.GONE);

                                                }
                                            });


                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            // Toast.makeText(context, "Task completed", Toast.LENGTH_SHORT).show();


                                            String sentMessageImageDownloadUrl = String.valueOf(task.getResult());
                                            onMessageImageUploadListener.onUploadSuccess(messageIdArray[0] ,sentMessageImageDownloadUrl, sentMessageImageStorageReference.getPath() );
                                            startUploadImageView.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.GONE);
                                            pauseUploadImageView.setVisibility(View.GONE);
                                            resumeUploadImageView.setVisibility(View.GONE);
                                            successImageView.setVisibility(View.VISIBLE);


                                        }
                                    });

                        }
                    });
        }

        if(isVideoIncluded){
            videoMessageStyledPlayerView.setVisibility(View.VISIBLE);
//            displayE
        }

        senderIdHolderDummyTextView.setText(senderId);
        recipientIdHolderDummyTextView.setText(recipientId);
        messageIdHolderDummyTextView.setText(messageId);

        if(isCurrentUserTheSender){
//            LinearLayout.LayoutParams bodyMainCardViewMessageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//            bodyMainCardViewMessageLayoutParams.setMargins(100,10,10,10);
//            bodyMessageMainCardView.setLayoutParams(bodyMainCardViewMessageLayoutParams);
            leftDummyView.setVisibility(View.VISIBLE);
            messageStateIndicatorImageView.setVisibility(View.VISIBLE);
            rightDummyView.setVisibility(View.GONE);
            bodyMessageMainLinearLayout.setBackgroundColor(context.getColor(R.color.secondary_app_color));
//            LinearLayout.LayoutParams textMessageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//            LinearLayout.LayoutParams imageMessageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,170);
//            LinearLayout.LayoutParams videoMessageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,200);
//
//            textMessageLayoutParams.setMarginStart(100);
//            imageMessageLayoutParams.setMarginStart(100);
//            videoMessageLayoutParams.setMarginStart(100);

            messageSenderDisplayNameTextView.setGravity(Gravity.END);
            messageSenderDisplayNameTextView.setTextColor(Color.BLUE);
            textMessageTextView.setForegroundGravity(Gravity.END);
            textMessageTextView.setTextColor(Color.WHITE);
            dateSentTextView.setTextColor(Color.WHITE);



//            textMessageTextView.setLayoutParams(textMessageLayoutParams);
//            imageMessageImageView.setLayoutParams(imageMessageLayoutParams);
//            videoMessageStyledPlayerView.setLayoutParams(videoMessageLayoutParams);

            dateSentTextView.setGravity(Gravity.END);

//            mainLinearLayout.setPadding(100,10,10,10);


            if(!isLoadFromSnapshot) {
                if (!hasPendingWrites) {
                    messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_sent_12);


                    if (isDeliveredToRecipient) {
                        messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_delivered_12);
                        if (isSeenByRecipient) {
                            messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_seen_blue_eye_12);
                        }
                    }
                }
            }

            //Listen to when the message is delivered and or seen
            if(!isSeenByRecipient && !hasPendingWrites){
                messageDocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        boolean[] isDeliveredToRecipient = new boolean[1];
                        boolean[] isSeenByRecipient = new boolean[1];

                        isDeliveredToRecipient[0] = false;
                        isSeenByRecipient[0] = false;
                        if(value != null ) {
                            if(value.exists()) {

                                if (value.get("IS_DELIVERED_" +recipientId) != null) {
                                    isDeliveredToRecipient[0] = value.getBoolean("IS_DELIVERED_" + recipientId);
                                }
                                if (value.get("IS_SEEN_" +recipientId) != null) {
                                    isSeenByRecipient[0] = value.getBoolean("IS_SEEN_" + recipientId);
                                }

                                if(isDeliveredToRecipient[0]){
                                    messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_delivered_12);
                                }
                                if(isSeenByRecipient[0]){
                                    messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_seen_blue_eye_12);
                                }
                            }else{
                                messageStateIndicatorImageView.setImageResource(R.drawable.ic_baseline_error_12);

                            }}

                    }
                });
            }


        }
        else{
            //set the left separator visible to indicator that it was the current user who sent the message
            leftDummyView.setVisibility(View.GONE);
            rightDummyView.setVisibility(View.VISIBLE);
        }


        //the tag is not useful right now
        messageView.setTag(viewGroup.getChildCount());
        if(isLoadFromSnapshot || isSendImage) {
            viewGroup.addView(messageView);

            if(ChatRoomActivity.topScrollView != null){
                ChatRoomActivity.topScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        ChatRoomActivity.topScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }else{
            viewGroup.addView(messageView,0);

        }


        if(!isDeliveredToCurrentUser){
            //message has not delivered to current user and also it has not been seen by current user
            setIsAlreadyDeliveredAndReadFlag(context,allMessagesDocumentDirectoryReference,messageDocumentReference,currentSenderId);
        }
        else{
            if(!isSeenByCurrentUser){
                //message has delivered to current user but  it has not been seen by current user
                setIsAlreadySeenFlag(context,allMessagesDocumentDirectoryReference,messageDocumentReference,currentSenderId);
            }
        }

    }
    public static  void viewImagePreview(Context context,ImageView imageView,String url){
        Dialog[] alertDialog =new Dialog[1];
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        alertDialog[0] = new Dialog(context);
        alertDialog[0].setContentView(R.layout.image_preview_layout);
        alertDialog[0].setTitle("Photo");
        alertDialog[0].setCancelable(true);

        //        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view =  layoutInflater.inflate(R.layout.image_preview_layout,viewGroup);

//       View view = alertDialog[0].findViewById()

        ImageView imagePreview = alertDialog[0].findViewById(R.id.imagePreviewId);
        imageView.setDrawingCacheEnabled(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

//        new ResourcesCompat();
//        Drawable dr = ResourcesCompat.getDrawable(context.getResources(),R.id.resImage,context.getTheme());
//        Picasso.get().load().into();
        imagePreview.animate();
        imagePreview.setImageBitmap(imageView.getDrawingCache());
        imagePreview.animate();
//        viewGroup.removeView(view);

        Picasso.get().load(url).into(imagePreview, new Callback() {
            @Override
            public void onSuccess() {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            }

            @Override
            public void onError(Exception e) {

            }
        });
        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog[0].cancel();
            }
        });
//          builder.setOnItemSelectedListener(new )
//          view.setOutlineSpotShadowColor(Color.LTGRAY);
        alertDialog[0].show();
    }

    static void sendPlainMessage(Context context, FirebaseFirestore firebaseFirestore , DocumentReference messageDocumentDirectory, String senderUserId , String recipientUserId,String messageId,String messageBody, String imageDownLoadUrl , String videoDownloadUrl, String imageStorageReference,String videoStorageReference, boolean isPlainText, boolean isImage, boolean isVideo ,ActionCallback actionCallback){

        if(messageId == null){
            messageId = getRandomString(60);

        }
        HashMap<String,Object> messageDetails = new HashMap<>();
        messageDetails.put("MESSAGE_BODY",messageBody);
        messageDetails.put("SENDER_USER_ID",senderUserId);
        messageDetails.put("RECIPIENT_USER_ID",recipientUserId);
        messageDetails.put("IMAGE_DOWNLOAD_URL",imageDownLoadUrl);
        messageDetails.put("IMAGE_STORAGE_REFERENCE",imageStorageReference);
        messageDetails.put("VIDEO_DOWNLOAD_URL",videoDownloadUrl);
        messageDetails.put("VIDEO_STORAGE_REFERENCE",videoStorageReference);
        messageDetails.put("IS_PLAIN_TEXT_INCLUDED",isPlainText);
        messageDetails.put("IS_IMAGE_INCLUDED",isImage);
        messageDetails.put("IS_VIDEO_INCLUDED",isVideo);

        messageDetails.put("IS_DELIVERED_"+recipientUserId,false);
        messageDetails.put("IS_SEEN_"+recipientUserId,false);
        messageDetails.put("IS_DELIVERED_"+senderUserId,true);
        messageDetails.put("IS_SEEN_"+senderUserId,true);

//        messageDetails.put("",);
//        messageDetails.put("",);
//        messageDetails.put("",);
//        messageDetails.put("DATE_SENT",new TimeStamp().getDate());
        messageDetails.put("DATE_SENT_TIME_STAMP",FieldValue.serverTimestamp());

        messageDocumentDirectory.collection("MESSAGES").document(messageId).set(messageDetails).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                actionCallback.onFailed(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                actionCallback.onSuccess();

            }
        });
    }
    public static void deleteMessage(DocumentReference messageDocumentReference,String messageId,boolean isTextIncluded,boolean isImageIncluded,boolean isVideoIncluded ){
        HashMap<String,Object> messageDetails = new HashMap<>();
        //must include this field to show that the message is deleted
        messageDetails.put("MESSAGE_BODY","-MESSAGE DELETED-");
        //must include this flag to show that the message is deleted
        messageDetails.put("IS_PLAIN_TEXT_INCLUDED",true);

        if(isImageIncluded || isVideoIncluded ){

            messageDocumentReference.collection("MESSAGES").document(messageId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    StorageReference messageImageStorageReference = FirebaseStorage.getInstance().getReference().child(""+ documentSnapshot.get("IMAGE_STORAGE_REFERENCE"));
                    StorageReference messageVideoStorageReference = FirebaseStorage.getInstance().getReference().child(""+ documentSnapshot.get("VIDEO_STORAGE_REFERENCE"));
                    messageImageStorageReference.delete();
                    messageVideoStorageReference.delete();

                    messageDetails.put("IMAGE_DOWNLOAD_URL","DELETED");
                    messageDetails.put("IMAGE_STORAGE_REFERENCE","DELETED");
                    messageDetails.put("VIDEO_DOWNLOAD_URL","DELETED");
                    messageDetails.put("VIDEO_STORAGE_REFERENCE","DELETED");
                    messageDetails.put("IS_IMAGE_INCLUDED",false);
                    messageDetails.put("IS_VIDEO_INCLUDED",false);

                    messageDocumentReference.collection("MESSAGES").document(messageId).update(messageDetails).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
                }
            });

        }
        else{
            messageDocumentReference.collection("MESSAGES").document(messageId).update(messageDetails).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
//HashMap<String,Object> messageDeletedDetails = new HashMap<>();
//                messageDeletedDetails.put("",);
//                messageDeletedDetails.put("TOTAL_NUMBER_OF_MESSAGES",FieldValue.increment(-1L));

                }
            });
        }



    }

    public static DocumentReference generateMessageDocumentDirectory(FirebaseFirestore firebaseFirestore,String senderUserId, String recipientUserId,OnGenerateMessageDocumentDirectoryReferenceListener onGenerateMessageDocumentDirectoryReferenceListener){

        DocumentReference messageDocumentDirectoryReference = firebaseFirestore.collection("ALL_MESSAGES").document(senderUserId).collection("PARTNER_ID").document(recipientUserId);

        WriteBatch writeBatch = firebaseFirestore.batch();


        //add this to current message directory
        HashMap<String,Object> messageDocumentDirectoryReferenceDetails = new HashMap<>();
        messageDocumentDirectoryReferenceDetails.put("PARTNER_ID",recipientUserId);
        messageDocumentDirectoryReferenceDetails.put("CREATOR_ID",senderUserId);
        writeBatch.set(messageDocumentDirectoryReference,messageDocumentDirectoryReferenceDetails,SetOptions.merge());



        //add this to current user's directory
        HashMap<String,Object> messageSenderDocumentDetails = new HashMap<>();
        messageSenderDocumentDetails.put("PARTNER_ID",recipientUserId);
        messageSenderDocumentDetails.put("MESSAGE_DOCUMENT_DIRECTORY_ORDER_ID",senderUserId+"_"+recipientUserId);
        messageSenderDocumentDetails.put("IS_CREATOR",true);
        messageSenderDocumentDetails.put("CREATOR_ID",senderUserId);
//        messageSenderDocumentDetails.put("DATE_CREATED",new TimeStamp().getDate());
        messageSenderDocumentDetails.put("DATE_CREATED_TIME_STAMP",FieldValue.serverTimestamp());

        DocumentReference messageSenderDocumentDetailsReference = firebaseFirestore.collection("ALL_USERS").document(senderUserId).collection("ALL_MESSAGES_DIRECTORY_ID").document(recipientUserId);
        writeBatch.set(messageSenderDocumentDetailsReference,messageSenderDocumentDetails,SetOptions.merge());

        //add this to current recipient's directory
        HashMap<String,Object> messageRecipientDocumentDetails = new HashMap<>();
        messageRecipientDocumentDetails.put("PARTNER_ID",senderUserId);
        messageRecipientDocumentDetails.put("MESSAGE_DOCUMENT_DIRECTORY_ORDER_ID",senderUserId+"_"+recipientUserId);
        messageRecipientDocumentDetails.put("IS_CREATOR",false);
        messageRecipientDocumentDetails.put("CREATOR_ID",senderUserId);
//        messageRecipientDocumentDetails.put("DATE_CREATED",new TimeStamp().getDate());
        messageRecipientDocumentDetails.put("DATE_CREATED_TIME_STAMP",FieldValue.serverTimestamp());

        DocumentReference messageRecipientDocumentDetailsReference = firebaseFirestore.collection("ALL_USERS").document(recipientUserId).collection("ALL_MESSAGES_DIRECTORY_ID").document(senderUserId);
        writeBatch.set(messageRecipientDocumentDetailsReference,messageRecipientDocumentDetails,SetOptions.merge());

        writeBatch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onGenerateMessageDocumentDirectoryReferenceListener.onGenerateFailed(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        onGenerateMessageDocumentDirectoryReferenceListener.onGenerateSuccess(messageDocumentDirectoryReference);
                    }
                });


        return messageDocumentDirectoryReference ;
    }

    public static ImageView getRecipientImageView(){

        return GlobalValue.recipientImageView;
    }
    public static void setRecipientImageView(ImageView recipientImageView){
        recipientImageView.setDrawingCacheEnabled(true);
        GlobalValue.recipientImageView = recipientImageView;
    }
    public static Intent goToChatRoom(Context context, String recipientUserId, String recipientUserName, ImageView recipientImageView){
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra("RECIPIENT_USER_ID",recipientUserId);
        intent.putExtra("RECIPIENT_DISPLAY_NAME",recipientUserName);
//        context.startActivity(intent);

        setRecipientImageView(recipientImageView);
        return intent;
    }

    public interface  OnRefreshChatsTriggeredListener{
        void onRefreshTriggered();
    }
    public static void setOnChatsFragmentRefreshTriggeredListener(OnRefreshChatsTriggeredListener onRefreshChatsTriggeredListener){
        GlobalValue.onRefreshChatsTriggeredListener = onRefreshChatsTriggeredListener;
    }
    public static OnRefreshChatsTriggeredListener getOnChatsFragmentRefreshTriggeredListener( ){
        return GlobalValue.onRefreshChatsTriggeredListener;
    }
    public static void setIsAlreadyDeliveredAndReadFlag(Context context,DocumentReference allMessagesDocumentDirectoryReference,DocumentReference messageDocumentReference, String currentSenderId){

        if(isConnectedOnline(context)) {

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            WriteBatch batchWrite = firebaseFirestore.batch();

            Map<String, Object> messageValueDetails = new HashMap<>();
            messageValueDetails.put("IS_DELIVERED_" + currentSenderId, true);
            messageValueDetails.put("IS_SEEN_" + currentSenderId, true);
            batchWrite.update(messageDocumentReference, messageValueDetails);

            Map<String, Object> messageSentValueDetails = new HashMap<>();
            messageSentValueDetails.put("TOTAL_NUMBER_OF_UNSEEN_MESSAGES_" + currentSenderId, FieldValue.increment(-1L));
            batchWrite.update(allMessagesDocumentDirectoryReference, messageSentValueDetails);

            batchWrite.commit();
        }
    }
    public static void setIsAlreadyDeliveredFlag(Context context, DocumentReference messageDocumentReference, String currentSenderId) {
        if(isConnectedOnline(context)){

            Map<String, Object> messageValueDetails = new HashMap<>();
            messageValueDetails.put("IS_DELIVERED_" + currentSenderId, true);
            messageDocumentReference.update(messageValueDetails);
        }
    }
    public static void setIsAlreadySeenFlag(Context context, DocumentReference allMessagesDocumentDirectoryReference,DocumentReference messageDocumentReference, String currentSenderId) {

        if(isConnectedOnline(context)){
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            WriteBatch batchWrite = firebaseFirestore.batch();

            Map<String, Object> messageValueDetails = new HashMap<>();
            messageValueDetails.put("IS_SEEN_" + currentSenderId, true);
            batchWrite.update(messageDocumentReference, messageValueDetails);

            Map<String, Object> messageSentValueDetails = new HashMap<>();
            messageSentValueDetails.put("TOTAL_NUMBER_OF_UNSEEN_MESSAGES_" + currentSenderId, FieldValue.increment(-1L));
            batchWrite.update(allMessagesDocumentDirectoryReference, messageSentValueDetails);

            batchWrite.commit();
        }
    }


    public static BottomSheetFormBuilderWidget getAnswerForm(Context context, String authorId, String questionId, String parentId, String answerId, boolean isEdition, boolean isAnswer, @Nullable AnswerDataModel answerDataModel, @NonNull AnswerCallback answerCallback){

        boolean[] isPhotoIncluded = new boolean[1];

        BottomSheetFormBuilderWidget  bottomSheetCatalogFormBuilderWidget =  new BottomSheetFormBuilderWidget(context);



        LinearLayout imageLayout = new LinearLayout(context);
        imageLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        imageLayout.setLayoutDirection(LinearLayout.LAYOUT_DIRECTION_LTR);
        imageLayout.setOrientation(LinearLayout.HORIZONTAL);

        ImageView answerImageView = new ImageView(context);
        answerImageView.setLayoutParams(new LinearLayout.LayoutParams(0,200,1));
        answerImageView.setImageResource(R.drawable.ic_baseline_photo_camera_24);
        answerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GlobalValue.createPopUpMenu(context, R.menu.pick_image_menu, answerImageView, new GlobalValue.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClicked(MenuItem item) {
                        if(item.getItemId() == R.id.openImageGalleryId){
                            answerCallback.onImageGallerySelected(answerImageView);
                        }
                        else if(item.getItemId() == R.id.openCameraId){
                            answerCallback.onCameraSelected(answerImageView);
                        }
                        return true;
                    }
                });

            }
        });

        ImageView removeImageView = new ImageView(context);
        removeImageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        removeImageView.setImageResource(R.drawable.ic_outline_cancel_24);
        removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerImageView.setImageResource(R.drawable.ic_baseline_photo_camera_24);
                isPhotoIncluded[0] = false;
            }
        });
        imageLayout.addView(answerImageView,0);
        imageLayout.addView(removeImageView,1);

        bottomSheetCatalogFormBuilderWidget.addView(imageLayout);
        bottomSheetCatalogFormBuilderWidget.setTitle("Contribute your answer to the question")
                .setPositiveTitle("Answer");

        BottomSheetFormBuilderWidget.EditTextInput answerEditTextInput = new BottomSheetFormBuilderWidget.EditTextInput(context);
        answerEditTextInput.setHint("Enter your answer");
        bottomSheetCatalogFormBuilderWidget.addInputField(answerEditTextInput);

        if(isEdition && answerDataModel!=null) {
            answerId = answerDataModel.getAnswerId();
            answerEditTextInput.setText(answerDataModel.getAnswer());
            isPhotoIncluded[0] = answerDataModel.isPhotoIncluded();
            if( isPhotoIncluded[0]){
                Glide.with(context)
                        .load(answerDataModel.getAnswerPhotoDownloadUrl())
                        .centerCrop()
                        .into(answerImageView);
            }
        }

        final String finalAnswerId = answerId;
        bottomSheetCatalogFormBuilderWidget.setOnSubmit(new BottomSheetFormBuilderWidget.OnSubmitHandler(){
            @Override
            public void onSubmit(String title,String body) {
                //super.onSubmit(body);

//                if(answerImageView.getDrawable().equals(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_photo_camera_24,getTheme()))){
//                    isPhotoIncluded[0] = false;
//                }else{
//                    isPhotoIncluded[0] = true;
//                }
//

//                    if(isPhotoIncluded[0]){
//
//                    }else{
////                    postAnswer(body[0]);
//
//                    }

                if(title.isEmpty() && !isPhotoIncluded[0]){
                    Toast.makeText(context, "Please enter an answer", Toast.LENGTH_SHORT).show();
                }
                else {
                    answerCallback.onStart(finalAnswerId);

                    startPostAnswer(context, authorId, questionId,parentId, finalAnswerId, title, answerImageView, isPhotoIncluded[0], isEdition,isAnswer, new GlobalValue.ActionCallback() {
                        @Override
                        public void onSuccess() {
                            answerCallback.onSuccess(finalAnswerId);
                        }

                        @Override
                        public void onFailed(String errorMessage) {
                            answerCallback.onFailed(errorMessage);

                        }
                    });
                }

            }
        });
        bottomSheetCatalogFormBuilderWidget
                .render("Post");

        return bottomSheetCatalogFormBuilderWidget;
    }
    private static void sendAnswerDetailsToDatabase(Context context,String authorId,String questionId,String answer,String parentId,String answerId,boolean isPhotoIncluded,String answerPhotoDownloadUrl,boolean isEdition,boolean isAnswer,@NonNull GlobalValue.ActionCallback actionCallback) {
        WriteBatch writeBatch = GlobalValue.getFirebaseFirestoreInstance().batch();
        DocumentReference documentReference1 = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_QUESTIONS).document(questionId).collection(GlobalValue.ALL_ANSWERS).document(answerId);
        HashMap<String,Object> quizDetails = new HashMap<>();
        quizDetails.put(GlobalValue.QUESTION_ID,questionId);
        quizDetails.put(GlobalValue.ANSWER_ID,answerId);
        quizDetails.put(GlobalValue.PARENT_ID,parentId);
        quizDetails.put(GlobalValue.AUTHOR_ID,authorId);
        quizDetails.put(GlobalValue.CONTRIBUTOR_ID,GlobalValue.getCurrentUserId());
        quizDetails.put(GlobalValue.ANSWER_BODY,answer);
        quizDetails.put(GlobalValue.ANSWER_PHOTO_DOWNLOAD_URL,answerPhotoDownloadUrl);
        quizDetails.put(GlobalValue.IS_PHOTO_INCLUDED,isPhotoIncluded);
        quizDetails.put(GlobalValue.IS_ANSWER,isAnswer);
        if(isEdition){
            quizDetails.put(GlobalValue.DATE_EDITED_TIME_STAMP, FieldValue.serverTimestamp());

        }else {
            quizDetails.put(GlobalValue.DATE_EDITED_TIME_STAMP, FieldValue.serverTimestamp());
            quizDetails.put(GlobalValue.DATE_CREATED_TIME_STAMP, FieldValue.serverTimestamp());
        }
        writeBatch.set(documentReference1,quizDetails, SetOptions.merge());

        if (!isEdition) {
            if (isAnswer) {
                //if it is answer then increment the number of answers to a question but if it's just a reply do otherwise
                DocumentReference questionReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_QUESTIONS).document(questionId);
                HashMap<String, Object> questionDetails = new HashMap<>();
                questionDetails.put(GlobalValue.TOTAL_NUMBER_OF_ANSWER, FieldValue.increment(1L));
                questionDetails.put(GlobalValue.ANSWER_CONTRIBUTORS_LIST, FieldValue.arrayUnion(GlobalValue.getCurrentUserId()));
                writeBatch.set(questionReference, questionDetails, SetOptions.merge());


                DocumentReference userReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId());
                HashMap<String, Object> userDetails = new HashMap<>();
                userDetails.put(GlobalValue.TOTAL_NUMBER_OF_ANSWER_CONTRIBUTED, FieldValue.increment(1L));
                userDetails.put(GlobalValue.QUESTIONS_ANSWERED_LIST, FieldValue.arrayUnion(questionId));
                writeBatch.set(userReference, userDetails, SetOptions.merge());

            }else{
                DocumentReference answerReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_QUESTIONS).document(questionId).collection(GlobalValue.ALL_ANSWERS).document(parentId);
                HashMap<String, Object> answernDetails = new HashMap<>();
                answernDetails.put(GlobalValue.TOTAL_REPLIES, FieldValue.increment(1L));
                answernDetails.put(GlobalValue.REPLY_CONTRIBUTORS_LIST, FieldValue.arrayUnion(GlobalValue.getCurrentUserId()));
                writeBatch.set(answerReference, answernDetails, SetOptions.merge());

            }

        }


        writeBatch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        actionCallback.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        actionCallback.onSuccess();
                    }
                });

    }
    private static void startPostAnswer(Context context,String authorId,String questionId,String parentId,String answerId, String answer, ImageView answerPhoto, boolean isPhotoIncluded, boolean isEdition,boolean isAnswer,@NonNull GlobalValue.ActionCallback actionCallback){

        if(isPhotoIncluded || answerPhoto!=null) {
            StorageReference answerPhotoStorageReference = GlobalValue.getFirebaseStorageInstance().getReference().child(GlobalValue.ALL_QUESTIONS + "/" + questionId + "/" + GlobalValue.ALL_ANSWERS + "/" + answerId + "/" + GlobalValue.PHOTOS + ".PNG");
            answerPhoto.setDrawingCacheEnabled(true);
            Bitmap coverPhotoBitmap = answerPhoto.getDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            coverPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 5, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            UploadTask coverPhotoUploadTask = answerPhotoStorageReference.putBytes(bytes);

            coverPhotoUploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    answerPhotoUploadListener.onFailed(e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    coverPhotoUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            return answerPhotoStorageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String answerPhotoDownloadUrl = String.valueOf(task.getResult());
//                            answerPhotoUploadListener.onSuccess(answer, isPhotoIncluded, isEdition, answerPhotoDownloadUrl);

                            sendAnswerDetailsToDatabase(context,authorId,questionId,answer,parentId, answerId, isPhotoIncluded, answerPhotoDownloadUrl, isEdition,isAnswer, new ActionCallback() {
                                @Override
                                public void onSuccess() {
                                    actionCallback.onSuccess();
                                }

                                @Override
                                public void onFailed(String errorMessage) {
                                    actionCallback.onFailed(errorMessage);

                                }
                            });
                        }
                    });
                }
            });
        }else{
            sendAnswerDetailsToDatabase(context,authorId,questionId,answer,parentId, answerId, isPhotoIncluded, "", isEdition,isAnswer, new GlobalValue.ActionCallback() {
                @Override
                public void onSuccess() {
                    actionCallback.onSuccess();

                }

                @Override
                public void onFailed(String errorMessage) {
                    actionCallback.onFailed(errorMessage);

                }
            });
        }

    }

    public static void deleteAnswer(String questionId,@Nullable String parentId,String answerId, String authorId,boolean isAnswer, ActionCallback actionCallback ){

        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();
        DocumentReference documentReference1 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_QUESTIONS).document(questionId).collection(GlobalValue.ALL_ANSWERS).document(answerId);
        writeBatch.delete(documentReference1);


        if(isAnswer) {
            DocumentReference documentReference3 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_QUESTIONS).document(questionId);
            HashMap<String, Object> answerDetails3 = new HashMap<>();
            answerDetails3.put(GlobalValue.TOTAL_NUMBER_OF_ANSWER, FieldValue.increment(-1L));
            writeBatch.set(documentReference3, answerDetails3, SetOptions.merge());

        }else{
            DocumentReference documentReference4 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_QUESTIONS).document(questionId).collection(GlobalValue.ALL_ANSWERS).document(parentId);
            HashMap<String, Object> answerDetails4 = new HashMap<>();
            answerDetails4.put(GlobalValue.TOTAL_REPLIES, FieldValue.increment(-1L));
            writeBatch.set(documentReference4, answerDetails4, SetOptions.merge());
        }


        writeBatch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        actionCallback.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        actionCallback.onSuccess();
                    }
                });
    }

    public static void voteAnswer(AnswerDataModel answerDataModel,boolean isUpVote, ActionCallback actionCallback){
        //check if user has upVoted or downVoted, if so, then return;
        if((isUpVote && answerDataModel.getUpVotersIdList().contains(getCurrentUserId())) || (!isUpVote && answerDataModel.getDownVotersIdList().contains(getCurrentUserId())))return;
        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();

        DocumentReference documentReference4 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_QUESTIONS).document(answerDataModel.getQuestionId()).collection(GlobalValue.ALL_ANSWERS).document(answerDataModel.getAnswerId());
        HashMap<String, Object> answerDetails4 = new HashMap<>();
        if(isUpVote){
            answerDetails4.put(GlobalValue.TOTAL_UP_VOTES, FieldValue.increment(1L));
            answerDetails4.put(GlobalValue.UP_VOTERS_ID_LIST, FieldValue.arrayUnion(getCurrentUserId()));

            if(answerDataModel.getDownVotersIdList().contains(getCurrentUserId())){
                answerDetails4.put(GlobalValue.TOTAL_DOWN_VOTES, FieldValue.increment(-1L));
                answerDetails4.put(GlobalValue.DOWN_VOTERS_ID_LIST, FieldValue.arrayRemove(getCurrentUserId()));
            }
        }else{
            answerDetails4.put(GlobalValue.TOTAL_DOWN_VOTES, FieldValue.increment(1L));
            answerDetails4.put(GlobalValue.DOWN_VOTERS_ID_LIST, FieldValue.arrayUnion(getCurrentUserId()));

            if(answerDataModel.getUpVotersIdList().contains(getCurrentUserId())){
                answerDetails4.put(GlobalValue.TOTAL_UP_VOTES, FieldValue.increment(-1L));
                answerDetails4.put(GlobalValue.UP_VOTERS_ID_LIST, FieldValue.arrayRemove(getCurrentUserId()));
            }
        }
        writeBatch.set(documentReference4, answerDetails4, SetOptions.merge());


        writeBatch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        actionCallback.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //UPDATE THE VOTERS LIST IN THE DATA MODEL OF THE ANSWER
                        if(isUpVote){
                            answerDataModel.getUpVotersIdList().add(getCurrentUserId());
                            answerDataModel.setTotalUpVotes(answerDataModel.getTotalUpVotes()+1);

                            if(answerDataModel.getDownVotersIdList().contains(getCurrentUserId())){
                                answerDataModel.getDownVotersIdList().remove(getCurrentUserId());
                                answerDataModel.setTotalDownVotes(answerDataModel.getTotalDownVotes()-1);

                            }
                        }else{
                            answerDataModel.getDownVotersIdList().add(getCurrentUserId());
                            answerDataModel.setTotalDownVotes(answerDataModel.getTotalDownVotes()+1);

                            if(answerDataModel.getUpVotersIdList().contains(getCurrentUserId())){
                                answerDataModel.getUpVotersIdList().remove(getCurrentUserId());
                                answerDataModel.setTotalUpVotes(answerDataModel.getTotalUpVotes()-1);

                            }
                        }
                        actionCallback.onSuccess();
                    }
                });
    }


    public static void comment(CommentDataModel commentDataModel, ActionCallback actionCallback ){
        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();
        DocumentReference documentReference1 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(commentDataModel.getAuthorId()).collection(GlobalValue.MY_UPDATES_COMMENT).document(commentDataModel.getCommentId());
        HashMap<String,Object> commentDetails = new HashMap<>();
        commentDetails.put(GlobalValue.COMMENT_POSTER_ID,commentDataModel.getCommentPosterId());
        commentDetails.put(GlobalValue.COMMENT_DESCRIPTION,commentDataModel.getDescription());
        commentDetails.put(GlobalValue.COMMENT_COVER_PHOTO_DOWNLOAD_URL,commentDataModel.getCoverDownloadUrl());
        commentDetails.put(GlobalValue.UPDATE_ID,commentDataModel.getUpdateId());
        commentDetails.put(GlobalValue.AUTHOR_ID,commentDataModel.getAuthorId());
        commentDetails.put(GlobalValue.PARENT_COMMENT_ID,commentDataModel.getParentCommentId());
        commentDetails.put(GlobalValue.COMMENT_ID,commentDataModel.getCommentId());
        commentDetails.put(GlobalValue.HAS_PARENT_COMMENT,commentDataModel.hasParentComment());
        commentDetails.put(GlobalValue.HAS_REPLIES,commentDataModel.hasReplies());
        commentDetails.put(GlobalValue.IS_HIDDEN_BY_AUTHOR,commentDataModel.isHiddenByAuthor());
        commentDetails.put(GlobalValue.IS_HIDDEN_BY_POSTER,commentDataModel.isHiddenByPoster());
//        commentDetails.put(GlobalValue.TOTAL_REPLIES,FieldValue.increment(1L));
//        commentDetails.put(GlobalValue.TOTAL_LIKES,FieldValue.increment(1L));
//        commentDetails.put(GlobalValue.TOTAL_DISLIKES,FieldValue.increment(1L));
        commentDetails.put(GlobalValue.REPLIERS_ID_LIST,commentDataModel.getRepliersIdList());
        commentDetails.put(GlobalValue.LIKERS_ID_LIST,commentDataModel.getLikersIdList());
//        commentDetails.put(GlobalValue.DISLIKERS_ID_LIST,commentDataModel.getDisLikersIdList());
        commentDetails.put(GlobalValue.DATE_CREATED_TIME_STAMP,FieldValue.serverTimestamp());
        writeBatch.set(documentReference1,commentDetails,SetOptions.merge());

        DocumentReference documentReference2 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(getCurrentUserId()).collection(GlobalValue.OTHER_UPDATES_COMMENT).document(commentDataModel.getUpdateId());
        HashMap<String,Object> commentDetails2 = new HashMap<>();
        commentDetails2.put(GlobalValue.AUTHOR_ID_LIST,FieldValue.arrayUnion(commentDataModel.getAuthorId()));
        commentDetails2.put(GlobalValue.UPDATE_ID_LIST,FieldValue.arrayUnion(commentDataModel.getUpdateId()));
        if(commentDataModel.hasParentComment()) {
            commentDetails2.put(GlobalValue.PARENT_COMMENT_ID_LIST, FieldValue.arrayUnion(commentDataModel.getUpdateId()));
        }
        writeBatch.set(documentReference2,commentDetails2,SetOptions.merge());


        if(commentDataModel.hasParentComment()) {
            DocumentReference documentReference4 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(commentDataModel.getAuthorId()).collection(GlobalValue.MY_UPDATES_COMMENT).document(commentDataModel.getParentCommentId());
            HashMap<String, Object> commentDetails4 = new HashMap<>();
            commentDetails4.put(GlobalValue.TOTAL_REPLIES, FieldValue.increment(1L));
            writeBatch.set(documentReference4, commentDetails4, SetOptions.merge());
        }else{
                DocumentReference documentReference3 = getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_UPDATES).document(commentDataModel.getUpdateId());
                HashMap<String, Object> commentDetails3 = new HashMap<>();
                commentDetails3.put(GlobalValue.TOTAL_COMMENTS, FieldValue.increment(1L));
                commentDetails3.put(GlobalValue.COMMENT_CONTRIBUTORS_ID_LIST, FieldValue.arrayUnion(getCurrentUserId()));
                writeBatch.set(documentReference3, commentDetails3, SetOptions.merge());

        }


        writeBatch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        actionCallback.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        actionCallback.onSuccess();
                    }
                });
    }
    public static void deleteComment(String commentId,String parentCommentId,String updateId,  String authorId,boolean hasParentComment, ActionCallback actionCallback ){
        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();
        DocumentReference documentReference1 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(authorId).collection(GlobalValue.MY_UPDATES_COMMENT).document(commentId);
        writeBatch.delete(documentReference1);


        if(hasParentComment) {
            DocumentReference documentReference4 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(authorId).collection(GlobalValue.MY_UPDATES_COMMENT).document(parentCommentId);
            HashMap<String, Object> commentDetails4 = new HashMap<>();
            commentDetails4.put(GlobalValue.TOTAL_COMMENTS, FieldValue.increment(-1L));
            writeBatch.set(documentReference4, commentDetails4, SetOptions.merge());
        }else{
                DocumentReference documentReference3 = getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_UPDATES).document(updateId);
                HashMap<String, Object> commentDetails3 = new HashMap<>();
                commentDetails3.put(GlobalValue.TOTAL_COMMENTS, FieldValue.increment(-1L));
                writeBatch.set(documentReference3, commentDetails3, SetOptions.merge());

        }


        writeBatch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        actionCallback.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        actionCallback.onSuccess();
                    }
                });
    }


    public static void likeUpdate(Context context, UpdateDataModel updateDataModel, boolean isIncreaseCount, ActionCallback actionCallback ) {
        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();

        DocumentReference documentReference1 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(getCurrentUserId()).collection(GlobalValue.LIKED_UPDATES).document(updateDataModel.getUpdateId());
        if(isIncreaseCount){
            HashMap<String, Object> updateDetails1 = new HashMap<>();
            updateDetails1.put(GlobalValue.AUTHOR_ID, updateDataModel.getAuthorId());
            updateDetails1.put(GlobalValue.UPDATE_ID, updateDataModel.getUpdateId());
            writeBatch.set(documentReference1, updateDetails1, SetOptions.merge());
        }else{
            writeBatch.delete(documentReference1);
        }
            DocumentReference documentReference3 = getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_UPDATES).document(updateDataModel.getUpdateId());
            HashMap<String, Object> commentDetails3 = new HashMap<>();
            if(isIncreaseCount){
                commentDetails3.put(GlobalValue.TOTAL_LIKES, FieldValue.increment(1L));
                commentDetails3.put(GlobalValue.LIKERS_ID_LIST, FieldValue.arrayUnion(getCurrentUserId()));

            }else{
                commentDetails3.put(GlobalValue.TOTAL_LIKES, FieldValue.increment(-1L));
                commentDetails3.put(GlobalValue.LIKERS_ID_LIST, FieldValue.arrayRemove(getCurrentUserId()));

            }
            writeBatch.set(documentReference3, commentDetails3, SetOptions.merge());


        DocumentReference userReference3 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(getCurrentUserId());
        HashMap<String, Object> userDetails3 = new HashMap<>();
        if(isIncreaseCount){
            userDetails3.put(GlobalValue.LIKED_UPDATES_LIST, FieldValue.arrayUnion(updateDataModel.getUpdateId()));
        }else{
            userDetails3.put(GlobalValue.LIKED_UPDATES_LIST, FieldValue.arrayRemove(updateDataModel.getUpdateId()));
        }
        writeBatch.set(userReference3, userDetails3, SetOptions.merge());

        writeBatch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        actionCallback.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        actionCallback.onSuccess();
                        recordLikedUpdate( context, updateDataModel.getUpdateId(),isIncreaseCount);
                        if(updateDataModel!=null) {
                            if (isIncreaseCount) {
                                if (!updateDataModel.getLikersIdList().contains(getCurrentUserId())) {
                                    updateDataModel.getLikersIdList().add(getCurrentUserId());
                                }
                            } else {
                                updateDataModel.getLikersIdList().remove(getCurrentUserId());
                            }
                        }
                    }
                });
    }

    public static boolean isUpdateLiked(Context context,String updateId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName()+getCurrentUserId(), MODE_PRIVATE);
        String likedList = sharedPreferences.getString(GlobalValue.LIKED_UPDATES_LIST,"");

        return likedList.contains(updateId);
    }
    public static void recordLikedUpdate(Context context,String updateId,boolean isIncreaseCount){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName()+getCurrentUserId(), MODE_PRIVATE);
        String oldList = sharedPreferences.getString(GlobalValue.LIKED_UPDATES_LIST,"");
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!oldList.contains(updateId)) {

            editor.putString(GlobalValue.LIKED_UPDATES_LIST, oldList + updateId + "-NEXT-");
        }else{
            if(!isIncreaseCount) {
                oldList = oldList.replace(updateId,"");
                editor.putString(GlobalValue.LIKED_UPDATES_LIST, oldList + updateId + "-NEXT-");
            }
        }
        editor.apply();

    }


    public static void likeComment(Context context,CommentDataModel commentDataModel,String commentId, String updateId, String authorId,boolean isIncreaseCount, ActionCallback actionCallback ) {
        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();

        DocumentReference documentReference1 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(getCurrentUserId()).collection(GlobalValue.LIKED_COMMENTS).document(commentId);
        if(isIncreaseCount){
            HashMap<String, Object> commentDetails1 = new HashMap<>();
            commentDetails1.put(GlobalValue.AUTHOR_ID, authorId);
            commentDetails1.put(GlobalValue.UPDATE_ID, updateId);
            commentDetails1.put(GlobalValue.COMMENT_ID, commentId);
            commentDetails1.put(GlobalValue.LIKERS_ID_LIST, FieldValue.arrayUnion(getCurrentUserId()));
            writeBatch.set(documentReference1, commentDetails1, SetOptions.merge());
        }else{
            writeBatch.delete(documentReference1);
        }

        DocumentReference documentReference4 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(authorId).collection(GlobalValue.MY_UPDATES_COMMENT).document(commentId);
        HashMap<String, Object> commentDetails4 = new HashMap<>();
        if(isIncreaseCount){
            commentDetails4.put(GlobalValue.TOTAL_LIKES, FieldValue.increment(1L));
        }else{
            commentDetails4.put(GlobalValue.TOTAL_LIKES, FieldValue.increment(-1L));
        }
        writeBatch.set(documentReference4, commentDetails4, SetOptions.merge());


        DocumentReference userReference3 = getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(getCurrentUserId());
        HashMap<String, Object> userDetails3 = new HashMap<>();
        if(isIncreaseCount){
            userDetails3.put(GlobalValue.LIKED_UPDATE_COMMENT_LIST, FieldValue.arrayUnion(commentId));
        }else{
            userDetails3.put(GlobalValue.LIKED_UPDATE_COMMENT_LIST, FieldValue.arrayRemove(commentId));
        }
        writeBatch.set(userReference3, userDetails3, SetOptions.merge());

        writeBatch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        actionCallback.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        actionCallback.onSuccess();
//                        recordLikedUpdateComment( context, commentId,isIncreaseCount);
                        if(isIncreaseCount) {
                            if (!commentDataModel.getLikersIdList().contains(getCurrentUserId())) {
                                commentDataModel.getLikersIdList().add(getCurrentUserId());
                            }
                        }else{
                            commentDataModel.getLikersIdList().remove(getCurrentUserId());
                        }
                    }
                });
    }
    public static boolean isCommentLiked(Context context,String commentId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName()+getCurrentUserId(), MODE_PRIVATE);
        String likedList = sharedPreferences.getString(GlobalValue.LIKED_UPDATE_COMMENT_LIST,"");

        return likedList.contains(commentId);
    }
    public static void recordLikedComment(Context context,String commentId,boolean isIncreaseCount){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName()+getCurrentUserId(), MODE_PRIVATE);
        String oldList = sharedPreferences.getString(GlobalValue.LIKED_UPDATE_COMMENT_LIST,"");
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!oldList.contains(commentId)) {

            editor.putString(GlobalValue.LIKED_UPDATE_COMMENT_LIST, oldList + commentId + "-NEXT-");
        }else{
            if(!isIncreaseCount) {
                oldList = oldList.replace(commentId,"");
                editor.putString(GlobalValue.LIKED_UPDATE_COMMENT_LIST, oldList + commentId + "-NEXT-");
            }
        }
        editor.apply();

    }




    public static  Snackbar createSnackBar2(Context context , View view,String text,String buttonLabel,int lengthPeriod,View.OnClickListener onClickListener){
        Snackbar snackBar = Snackbar.make(view,text,lengthPeriod);
        snackBar.setAction(buttonLabel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(view);
                snackBar.dismiss();
            }
        });
        snackBar.show();

        return snackBar;
    }

    public static ShimmerFrameLayout showShimmerLayout(Context context, ViewGroup viewGroup ){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ShimmerFrameLayout shimmerFrameLayout = (ShimmerFrameLayout) layoutInflater.inflate(R.layout.progress_indicator_shimmer_layout,viewGroup,false);
//        ShimmerFrameLayout shimmerFrameLayout  = view.findViewById(R.id.progressIndicatorShimmerId);
        shimmerFrameLayout.startShimmer();
        viewGroup.addView(shimmerFrameLayout);
        return shimmerFrameLayout;
    }
    public static void removeShimmerLayout(ViewGroup viewGroup, ShimmerFrameLayout shimmerFrameLayout){
        if(shimmerFrameLayout !=null){
            shimmerFrameLayout.stopShimmer();
            viewGroup.removeView(shimmerFrameLayout);
        }
        shimmerFrameLayout = null;
    }

    public static ArrayList<Integer> getVviewLimitList(){
        int[] viewLimits = {100,200,300,400,500,600,700,800,900,1000,1200,1500,1800,2000,2300,2500,3000,3500,4000,4500,5000,5500,6000,6500,7000,7500,8000,8500,9000,9500,10000};
        ArrayList<Integer> viewLimitList = new ArrayList<>() ;
        for(int limit :viewLimits){
            viewLimitList.add(limit);
        }

        return viewLimitList;
    }
    public static void sendNotificationToUsers(String notificationType,String notificationId,ArrayList<String>receiversIdList,ArrayList<String> modelInfoList,String title,String message,ActionCallback actionCallback){
        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();


        for(int i=0; i<receiversIdList.size();i++){
            DocumentReference notificationReference = getFirebaseFirestoreInstance().collection(ALL_USERS).document(receiversIdList.get(i)).collection(PERSONALIZED_NOTIFICATIONS).document(notificationId);
            HashMap<String,Object> notesInfo = new HashMap<>();
            notesInfo.put(NOTIFICATION_TYPE,notificationType);
            notesInfo.put(NOTIFICATION_ID,notificationId);
            notesInfo.put(NOTIFICATION_SENDER_ID,getCurrentUserId());
            notesInfo.put(NOTIFICATION_TITLE,title);
            notesInfo.put(NOTIFICATION_MESSAGE,message);
            notesInfo.put(NOTIFICATION_MODEL_INFO_LIST,modelInfoList);
            notesInfo.put(DATE_NOTIFIED_TIME_STAMP,FieldValue.serverTimestamp());
            notesInfo.put(IS_SEEN,false);
            writeBatch.set(notificationReference,notesInfo,SetOptions.merge());

            DocumentReference userReference = getFirebaseFirestoreInstance().collection(ALL_USERS).document(receiversIdList.get(i));
            HashMap<String,Object> userInfo = new HashMap<>();
            userInfo.put(DATE_PERSONALIZED_NOTIFICATION_LAST_SEEN_TIME_STAMP,FieldValue.serverTimestamp());
            userInfo.put(THERE_IS_NEW_PERSONALIZED_NOTIFICATION,true);
            writeBatch.set(userReference,userInfo,SetOptions.merge());
        }
        writeBatch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(actionCallback !=null) {
                            actionCallback.onFailed(e.getMessage());
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if(actionCallback !=null) {
                            actionCallback.onSuccess();
                        }
                    }
                });


    }
    public static void declineAdvert(String ownerId,String modelId,boolean isCustomAd,boolean isProduct,boolean isBusinessPage,boolean isPeople,ActionCallback actionCallback){
        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();
//        DocumentReference documentReference = getFirebaseFirestoreInstance().collection(ALL_ADVERTS).document(ALL_ADVERTS);
//        HashMap<String,Object> details = new HashMap<>();
//        details.put(ADVERTS_ID_LIST,FieldValue.arrayRemove(modelId));
//        details.put(modelId,FieldValue.delete());
//        writeBatch.set(documentReference,details,SetOptions.merge());

        if(isCustomAd){
            DocumentReference documentReference1 = getFirebaseFirestoreInstance().collection(PLATFORM_ADVERTS).document(modelId);
            HashMap<String,Object> details1 = new HashMap<>();
            details1.put(IS_ADVERT_REQUESTED,false);
            writeBatch.set(documentReference1,details1,SetOptions.merge());

        }
        else if(isProduct){
            DocumentReference documentReference2 = getFirebaseFirestoreInstance().collection(ALL_PRODUCTS).document(modelId);
            HashMap<String,Object> details2 = new HashMap<>();
            details2.put(IS_ADVERT_REQUESTED,false);
            writeBatch.set(documentReference2,details2,SetOptions.merge());

        }
        else if(isBusinessPage){
            DocumentReference documentReference3 = getFirebaseFirestoreInstance().collection(PAGES).document(modelId);
            HashMap<String,Object> details3 = new HashMap<>();
            details3.put(IS_ADVERT_REQUESTED,false);
            writeBatch.set(documentReference3,details3,SetOptions.merge());

        }
        else if(isPeople){
            DocumentReference documentReference4 = getFirebaseFirestoreInstance().collection(ALL_USERS).document(modelId);
            HashMap<String,Object> details4 = new HashMap<>();
            details4.put(IS_ADVERT_REQUESTED,false);
            writeBatch.set(documentReference4,details4,SetOptions.merge());

        }

        writeBatch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(actionCallback !=null) {
                            actionCallback.onFailed(e.getMessage());
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if(actionCallback !=null) {
                            //TODO : SEND NOTIFICATION TO THE OWNER
                            //carries the info about the quiz
                            ArrayList<String> modelInfo = new ArrayList<>();
                            modelInfo.add(modelId);

                            ArrayList<String> recipientIds = new ArrayList<String>();
                            recipientIds.add(ownerId);

                            //fires out the notification
                            GlobalValue.sendNotificationToUsers(GlobalValue.NOTIFICATION_TYPE_ADVERT_DECLINED,GlobalValue.getRandomString(60),recipientIds,modelInfo,"Boost declined","Your requested boost is declined",null);


                            actionCallback.onSuccess();

                        }
                    }
                });
        return;

    }
    public static void approveAdvert(String ownerId,String modelId,long numberOfRequestedViews,boolean isCustomAd,boolean isProduct,boolean isBusinessPage,boolean isPeople,ActionCallback actionCallback){
        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();
        DocumentReference documentReference = getFirebaseFirestoreInstance().collection(ALL_ADVERTS).document(ALL_ADVERTS);
        HashMap<String,Object> details = new HashMap<>();
        details.put(ADVERTS_ID_LIST,FieldValue.arrayUnion(modelId));
        //add list that contains the information of the item being advertised
        ArrayList<Object>modelInfo = new ArrayList<>();
        //the id of the item to be advertised; String dataType
        modelInfo.add(0,modelId);
        //views of the advert; Long dataType
        modelInfo.add(1,numberOfRequestedViews);
        //TOTAL views already accummulated
        modelInfo.add(2,0L);
        if(isCustomAd){
            //the type of the advert; String dataType
            modelInfo.add(3,CUSTOM_ADVERT_TYPE);

        }
        else if(isProduct){
            //the type of the advert; String dataType
            modelInfo.add(3,PRODUCT_ADVERT_TYPE);

        }
        else if(isBusinessPage){
            //the type of the advert; String dataType
            modelInfo.add(3,BUSINESS_PAGE_ADVERT_TYPE);

        }
        else if(isPeople){
            //the type of the advert; String dataType
            modelInfo.add(3,PEOPLE_ADVERT_TYPE);

        }
        details.put(modelId,modelInfo);
        writeBatch.set(documentReference,details,SetOptions.merge());

        if(isCustomAd){
            DocumentReference documentReference1 = getFirebaseFirestoreInstance().collection(PLATFORM_ADVERTS).document(modelId);
            HashMap<String,Object> details1 = new HashMap<>();
            details1.put(IS_ADVERT_REQUESTED,false);
            details1.put(IS_ADVERT_RUNNING,true);
            writeBatch.set(documentReference1,details1,SetOptions.merge());

        }
        else if(isProduct){
            DocumentReference documentReference2 = getFirebaseFirestoreInstance().collection(ALL_PRODUCTS).document(modelId);
            HashMap<String,Object> details2 = new HashMap<>();
            details2.put(IS_ADVERT_REQUESTED,false);
            details2.put(IS_ADVERT_RUNNING,true);
            writeBatch.set(documentReference2,details2,SetOptions.merge());

        }
        else if(isBusinessPage){
            DocumentReference documentReference3 = getFirebaseFirestoreInstance().collection(PAGES).document(modelId);
            HashMap<String,Object> details3 = new HashMap<>();
            details3.put(IS_ADVERT_REQUESTED,false);
            details3.put(IS_ADVERT_RUNNING,true);
            writeBatch.set(documentReference3,details3,SetOptions.merge());

        }
        else if(isPeople){
            DocumentReference documentReference4 = getFirebaseFirestoreInstance().collection(ALL_USERS).document(modelId);
            HashMap<String,Object> details4 = new HashMap<>();
            details4.put(IS_ADVERT_REQUESTED,false);
            details4.put(IS_ADVERT_RUNNING,true);
            writeBatch.set(documentReference4,details4,SetOptions.merge());

        }

        writeBatch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(actionCallback !=null) {
                            actionCallback.onFailed(e.getMessage());
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if(actionCallback !=null) {
                            //TODO : SEND NOTIFICATION TO THE OWNER
                            //carries the info about the quiz
                            ArrayList<String> modelInfo = new ArrayList<>();
                            modelInfo.add(modelId);

                            ArrayList<String> recipientIds = new ArrayList<String>();
                            recipientIds.add(ownerId);

                            //fires out the notification
                            GlobalValue.sendNotificationToUsers(GlobalValue.NOTIFICATION_TYPE_ADVERT_APPROVED,GlobalValue.getRandomString(60),recipientIds,modelInfo,"Boost Approved","Your requested boost is approved",null);


                            actionCallback.onSuccess();

                        }
                    }
                });
        return;

    }
    public static void requestAdvert(Context context,String ownerId,String modelId,boolean isCustomAd,boolean isProduct,boolean isBusinessPage,boolean isPeople,ActionCallback actionCallback){
        final int[] numberOfRequestedAdvertViews = {0};
        AlertDialog[] dialog = new AlertDialog[1];
       AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
       View advertRequestView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.advert_request_view,null,false);
        dialogBuilder.setView(advertRequestView);
        Spinner viewLimitSpinner = advertRequestView.findViewById(R.id.viewLimitSpinnerId);
        MaterialButton cancelButton = advertRequestView.findViewById(R.id.cancelButtonId);
        MaterialButton confirmBoostButton = advertRequestView.findViewById(R.id.confirmBoostButtonId);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(context,R.layout.support_simple_spinner_dropdown_item,GlobalValue.getVviewLimitList());
        viewLimitSpinner.setAdapter(adapter);
        viewLimitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                numberOfRequestedAdvertViews[0] = Integer.parseInt(adapterView.getSelectedItem()+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog[0].dismiss();
            }
        });
        confirmBoostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();
//        DocumentReference documentReference = getFirebaseFirestoreInstance().collection(ALL_ADVERTS).document(ALL_ADVERTS);
//        HashMap<String,Object> details = new HashMap<>();
//        details.put(ADVERTS_ID_LIST,FieldValue.arrayRemove(modelId));
//        details.put(modelId,FieldValue.delete());
//        writeBatch.set(documentReference,details,SetOptions.merge());

                if(isCustomAd){
                    DocumentReference documentReference1 = getFirebaseFirestoreInstance().collection(PLATFORM_ADVERTS).document(modelId);
                    HashMap<String,Object> details1 = new HashMap<>();
                    details1.put(IS_ADVERT_REQUESTED,true);
                    details1.put(TOTAL_NUMBER_OF_REQUESTED_ADVERT_VIEW, numberOfRequestedAdvertViews[0]);
                    writeBatch.set(documentReference1,details1,SetOptions.merge());

                }
                else if(isProduct){
                    DocumentReference documentReference2 = getFirebaseFirestoreInstance().collection(ALL_PRODUCTS).document(modelId);
                    HashMap<String,Object> details2 = new HashMap<>();
                    details2.put(IS_ADVERT_REQUESTED,true);
                    details2.put(TOTAL_NUMBER_OF_REQUESTED_ADVERT_VIEW, numberOfRequestedAdvertViews[0]);
                    writeBatch.set(documentReference2,details2,SetOptions.merge());

                }
                else if(isBusinessPage){
                    DocumentReference documentReference3 = getFirebaseFirestoreInstance().collection(PAGES).document(modelId);
                    HashMap<String,Object> details3 = new HashMap<>();
                    details3.put(IS_ADVERT_REQUESTED,true);
                    details3.put(TOTAL_NUMBER_OF_REQUESTED_ADVERT_VIEW, numberOfRequestedAdvertViews[0]);
                    writeBatch.set(documentReference3,details3,SetOptions.merge());

                }
                else if(isPeople){
                    DocumentReference documentReference4 = getFirebaseFirestoreInstance().collection(ALL_USERS).document(modelId);
                    HashMap<String,Object> details4 = new HashMap<>();
                    details4.put(IS_ADVERT_REQUESTED,true);
                    details4.put(TOTAL_NUMBER_OF_REQUESTED_ADVERT_VIEW, numberOfRequestedAdvertViews[0]);
                    writeBatch.set(documentReference4,details4,SetOptions.merge());

                }

                writeBatch.commit()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(actionCallback !=null) {
                                    actionCallback.onFailed(e.getMessage());
                                }
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if(actionCallback !=null) {
                                    //TODO : SEND NOTIFICATION TO THE OWNER
                                    //carries the info about the quiz
                                    ArrayList<String> modelInfo = new ArrayList<>();
                                    modelInfo.add(modelId);

                                    ArrayList<String> recipientIds = new ArrayList<String>();
                                    recipientIds.add(ownerId);

                                    //fires out the notification
                                    GlobalValue.sendNotificationToUsers(GlobalValue.NOTIFICATION_TYPE_ADVERT_DECLINED,GlobalValue.getRandomString(60),recipientIds,modelInfo,"Boost declined","Your requested boost is declined",null);


                                    actionCallback.onSuccess();

                                }
                            }
                        });


            }
        });
        dialogBuilder.setOnCancelListener(dialog1 -> {
            actionCallback.onFailed("Cancelled");
        });
        dialog[0] = dialogBuilder.setCancelable(true).create();
        dialogBuilder.show();

    }
    public static void markAdvertAsCompleted(String ownerId,String modelId,boolean isCustomAd,boolean isProduct,boolean isBusinessPage,boolean isPeople,ActionCallback actionCallback){
        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();
        DocumentReference documentReference = getFirebaseFirestoreInstance().collection(ALL_ADVERTS).document(ALL_ADVERTS);
        HashMap<String,Object> details = new HashMap<>();
        details.put(ADVERTS_ID_LIST,FieldValue.arrayRemove(modelId));
        details.put(modelId,FieldValue.delete());
        writeBatch.set(documentReference,details,SetOptions.merge());

        if(isCustomAd){
            DocumentReference documentReference1 = getFirebaseFirestoreInstance().collection(PLATFORM_ADVERTS).document(modelId);
            HashMap<String,Object> details1 = new HashMap<>();
            details1.put(IS_ADVERT_RUNNING,false);
            writeBatch.set(documentReference1,details1,SetOptions.merge());

        }
        else if(isProduct){
            DocumentReference documentReference2 = getFirebaseFirestoreInstance().collection(ALL_PRODUCTS).document(modelId);
            HashMap<String,Object> details2 = new HashMap<>();
            details2.put(IS_ADVERT_RUNNING,false);
            writeBatch.set(documentReference2,details2,SetOptions.merge());

        }
        else if(isBusinessPage){
            DocumentReference documentReference3 = getFirebaseFirestoreInstance().collection(PAGES).document(modelId);
            HashMap<String,Object> details3 = new HashMap<>();
            details3.put(IS_ADVERT_RUNNING,false);
            writeBatch.set(documentReference3,details3,SetOptions.merge());

        }
        else if(isPeople){
            DocumentReference documentReference4 = getFirebaseFirestoreInstance().collection(ALL_USERS).document(modelId);
            HashMap<String,Object> details4 = new HashMap<>();
            details4.put(IS_ADVERT_RUNNING,false);
            writeBatch.set(documentReference4,details4,SetOptions.merge());

        }

        writeBatch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(actionCallback !=null) {
                            actionCallback.onFailed(e.getMessage());
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if(actionCallback !=null) {
                            //TODO : SEND NOTIFICATION TO THE OWNER
                            //carries the info about the quiz
                            ArrayList<String> modelInfo = new ArrayList<>();
                            modelInfo.add(modelId);

                            ArrayList<String> recipientIds = new ArrayList<String>();
                            recipientIds.add(ownerId);

                            //fires out the notification
                            GlobalValue.sendNotificationToUsers(GlobalValue.NOTIFICATION_TYPE_ADVERT_COMPLETED,GlobalValue.getRandomString(60),recipientIds,modelInfo,"Boost Completed","Your boost has completed and so no longer running. you can boost again for better business",null);


                            actionCallback.onSuccess();

                        }
                    }
                });


    }
    public static void incrementAdvertViewCount(String modelId){
        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();
        DocumentReference documentReference = getFirebaseFirestoreInstance().collection(ALL_ADVERTS).document(ALL_ADVERTS);
        HashMap<String,Object> details = new HashMap<>();
        details.put(ADVERTS_ID_LIST,FieldValue.arrayUnion(modelId));
        //add list that contains the information of the item being advertised
        ArrayList<Object>modelInfo = new ArrayList<>();
        //TOTAL views already accummulated
        modelInfo.add(2,0L);

        details.put(modelId,modelInfo);
        writeBatch.set(documentReference,details,SetOptions.merge());


        writeBatch.commit();


    }

    public static View getPlatformAdvertView(Context context, LinearLayout advertView){
//        LinearLayout advertView = new LinearLayout(context);
//        advertView.setLayoutParams(new ViewGroup.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
//        advertView.setLayoutDirection(LinearLayout.LAYOUT_DIRECTION_LTR);


        WriteBatch writeBatch = getFirebaseFirestoreInstance().batch();
        DocumentReference documentReference = getFirebaseFirestoreInstance().collection(ALL_ADVERTS).document(ALL_ADVERTS);
        documentReference.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> advertIdList = documentSnapshot.get(GlobalValue.ADVERTS_ID_LIST)!=null ? (ArrayList<String>) documentSnapshot.get(GlobalValue.ADVERTS_ID_LIST) : new ArrayList<>();
                        String qualifiedModelIdToDisplay = "0";
                        String qualifiedModelTypeToDisplay = "0";

                        long previousMinimumAdvertItemViewCount = 900000000000000L;
                        for(int i=0; i<advertIdList.size(); i++){
                            //gets the info of the advertised item
                          ArrayList<Object> modelInfo = documentSnapshot.get(advertIdList.get(i))!=null ? (ArrayList<Object>) documentSnapshot.get(advertIdList.get(i)) : new ArrayList<>();
                          //id of the item being advertised
                           String modelId =  modelInfo.get(0)+"";
                            //views of the advert; Long dataType
                           long  numberOfRequestedViews = (long) modelInfo.get(1);
                           //total views already accumulated
                           long  numberOfViews = (long) modelInfo.get(2);
                                //the type of the advert; String dataType
                            String advertType =  modelInfo.get(3)+"";

                            if(numberOfViews<previousMinimumAdvertItemViewCount){
                                qualifiedModelIdToDisplay = modelId;
                                qualifiedModelTypeToDisplay = advertType;
                                previousMinimumAdvertItemViewCount = numberOfViews;

                            }

                        }
                        switch(qualifiedModelTypeToDisplay){
                            case CUSTOM_ADVERT_TYPE:
                                GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_ADVERTS).document(qualifiedModelIdToDisplay).get()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                String updateId = documentSnapshot.getId();
                                                String ownerId = "" + documentSnapshot.get(GlobalValue.PRODUCT_OWNER_USER_ID);
                                                String updateTitle = "" + documentSnapshot.get(GlobalValue.ADVERT_TITLE);
                                                String updateDescription = "" + documentSnapshot.get(GlobalValue.ADVERT_DESCRIPTION);
                                                String datePosted = documentSnapshot.get(GlobalValue.DATE_POSTED_TIME_STAMP) != null ? documentSnapshot.getTimestamp(GlobalValue.DATE_POSTED_TIME_STAMP).toDate() + "" : "Undefined";
                                                if (datePosted.length() > 10) {
                                                    datePosted = datePosted.substring(0, 10);
                                                }
                                                long viewCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_VIEWS) != null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_VIEWS) : 0L;
                                                long viewLimit = documentSnapshot.get(GlobalValue.ADVERT_VIEW_LIMIT) != null ? documentSnapshot.getLong(GlobalValue.ADVERT_VIEW_LIMIT) : 0L;
                                                ArrayList<String> imageUrlList = documentSnapshot.get(GlobalValue.ADVERT_IMAGE_DOWNLOAD_URL_ARRAY_LIST) != null ? (ArrayList<String>) documentSnapshot.get(GlobalValue.ADVERT_IMAGE_DOWNLOAD_URL_ARRAY_LIST) : new ArrayList<>();
                                                //ArrayList<String> viewersIdList = documentSnapshot.get(GlobalValue.ADVERT_VIEWERS_ID_ARRAY_LIST) != null ? (ArrayList<String>) documentSnapshot.get(GlobalValue.ADVERT_VIEWERS_ID_ARRAY_LIST) : new ArrayList<>();
                                                boolean isPrivate = documentSnapshot.get(GlobalValue.IS_PRIVATE) != null ? documentSnapshot.getBoolean(GlobalValue.IS_PRIVATE) : false;
                                                boolean isViewLimitExceeded = documentSnapshot.get(GlobalValue.IS_VIEW_EXCEEDED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_VIEW_EXCEEDED) : false;
                                                boolean isApproved = documentSnapshot.get(GlobalValue.IS_APPROVED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_APPROVED) : false;

                                                AdvertsDataModel advertDataModel = new AdvertsDataModel(updateId,ownerId, updateTitle, updateDescription, datePosted, imageUrlList, (int) viewCount,(int) viewLimit,isViewLimitExceeded, isPrivate,isApproved);

                                                View customAdvertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.product_item_layout,null,false);

                                                TextView titleTextView=  customAdvertView.findViewById(R.id.advertTitleTextViewId);
                                                 TextView datePostedTextView =  customAdvertView.findViewById(R.id.datePostedTextViewId);
                                                 TextView description =  customAdvertView.findViewById(R.id.descriptionTextViewId);
                                                 TextView viewCountTextView =  customAdvertView.findViewById(R.id.viewCountTextViewId);
                                                 ImageView icon = customAdvertView.findViewById(R.id.advertImageViewId);

                                                      titleTextView.setText(advertDataModel.getTitle());
                                        //        holder.datePosted.setText(productDataModel.getDatePosted());
                                        //        holder.description.setText(productDataModel.getProductDescription());
                                                    description.setText(advertDataModel.getDescription());
                                                    viewCountTextView.setText(""+advertDataModel.getNumOfViews());
                                                    datePostedTextView.setText(""+ advertDataModel.getDatePosted());

                                                    if(advertDataModel.getImageUrlList().size() == 0){
                                                       icon.setVisibility(View.GONE);
                                                    }else {
                                                        Picasso.get()
                                                                .load(advertDataModel.getImageUrlList().get(0))
                                                                .placeholder(R.drawable.agg_logo)
                                                                .into(icon);
                                                    }
                                                incrementAdvertViewCount(updateId);
                                                advertView.addView(customAdvertView);
                                            }
                                        });

                                break;
                            case PRODUCT_ADVERT_TYPE:

                                getFirebaseFirestoreInstance().collection(GlobalValue.ALL_PRODUCTS).document(qualifiedModelIdToDisplay).get().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                })
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                String productId = documentSnapshot.getId();
                                                String productTitle =""+ documentSnapshot.get(GlobalValue.PRODUCT_TITLE);
                                                String productOwnerId =""+ documentSnapshot.get(GlobalValue.PRODUCT_OWNER_USER_ID);
                                                String productPrice =""+ documentSnapshot.get(GlobalValue.PRODUCT_PRICE);
                                                String productDescription =""+ documentSnapshot.get(GlobalValue.PRODUCT_DESCRIPTION);
                                                String location = documentSnapshot.get(GlobalValue.LOCATION)+"";
                                                String phone = documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_PHONE_NUMBER) +"";
                                                String email = documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_EMAIL) +"";
                                                String residentialAddress = documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_ADDRESS) +"";
                                                String datePosted = documentSnapshot.get(GlobalValue.DATE_POSTED_TIME_STAMP)!=null ? documentSnapshot.getTimestamp(GlobalValue.DATE_POSTED_TIME_STAMP).toDate()+""  : "Undefined";
                                                if(datePosted.length()>10){
                                                    datePosted = datePosted.substring(0,10);
                                                }
                                                long productOrderCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_ORDERS)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_ORDERS) : 0L;
                                                long productNewOrderCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_NEW_ORDERS) : 0L;
                                                long productViewCount = documentSnapshot.get(GlobalValue.TOTAL_NUMBER_OF_VIEWS)!=null ? documentSnapshot.getLong(GlobalValue.TOTAL_NUMBER_OF_VIEWS) : 0L;
                                                ArrayList<String> imageUrlList = documentSnapshot.get(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL_ARRAY_LIST)!=null ? (ArrayList<String>) documentSnapshot.get(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL_ARRAY_LIST) : new ArrayList<>();
                                                boolean isPrivate = documentSnapshot.get(GlobalValue.IS_PRIVATE)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_PRIVATE) : false;

                                                boolean isFromSubmission = documentSnapshot.get(GlobalValue.IS_FROM_SUBMISSION) != null ? documentSnapshot.getBoolean(GlobalValue.IS_FROM_SUBMISSION) : false;
                                                boolean isApproved = documentSnapshot.get(GlobalValue.IS_APPROVED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_APPROVED) : false;
                                                boolean isSold = documentSnapshot.get(GlobalValue.IS_SOLD) != null ? documentSnapshot.getBoolean(GlobalValue.IS_SOLD) : false;

                                                boolean isAdvertRequested =  documentSnapshot.get(GlobalValue.IS_ADVERT_REQUESTED)!=null? documentSnapshot.getBoolean(GlobalValue.IS_ADVERT_REQUESTED): false;

                                                ProductDataModel productDataModel = new ProductDataModel(productId,productOwnerId,productTitle,productPrice,productDescription,location,phone,email,residentialAddress,isSold,datePosted,productViewCount,productOrderCount,productNewOrderCount,imageUrlList,isPrivate,isFromSubmission,isApproved,isAdvertRequested);


                                                View productView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.product_item_layout,null,false);


                                                 TextView title =  productView.findViewById(R.id.productTitleTextViewId);;
                                                 TextView datePostedTextView =  productView.findViewById(R.id.datePostedTextViewId);;
                                                 TextView locationTextView =  productView.findViewById(R.id.locationTextViewId);;
//                                                public TextView description;
                                                 TextView price =  productView.findViewById(R.id.productPriceTextViewId);
                                                 TextView productViewCountTextView =  productView.findViewById(R.id.productViewCountTextViewId);;
                                                 TextView productOrderCountTextView  =  productView.findViewById(R.id.productOrderCountTextViewId);;
                                                 TextView productNewOrderCountTextView  =  productView.findViewById(R.id.productNewOrderCountTextViewId);;
                                                 TextView soldIndicator =  productView.findViewById(R.id.soldIndicatorTextViewId);;
                                                 TextView phoneNumberTextView =  productView.findViewById(R.id.phoneNumberTextViewId);;
                                                 TextView emailAddressTextView  =  productView.findViewById(R.id.emailAddressTextViewId);;
                                                 TextView residentialAddressTextView =  productView.findViewById(R.id.residentialAddressTextViewId);;
                                                 ImageView icon = productView.findViewById(R.id.productImageViewId);;
                                                 Button orderActionButton = productView.findViewById(R.id.orderProductActionButtonId);;
                                                 Button soldIndicatorButton  =  productView.findViewById(R.id.soldIndicatorButtonId);;


                                                title.setText(productDataModel.getProductTitle());
                                                price.setText("Price: "+productDataModel.getProductPrice());
                                                productOrderCountTextView.setText(" "+ productDataModel.getProductOrderCount());
                                                productViewCountTextView.setText(" "+productDataModel.getProductViewCount());
                                                productNewOrderCountTextView.setText(""+ productDataModel.getProductNewOrderCount());
                                                phoneNumberTextView.setText("Phone No: "+ productDataModel.getPhone());
                                                emailAddressTextView.setText("Email: "+ productDataModel.getEmail());
                                                residentialAddressTextView.setText("Residence: "+ productDataModel.getResidentialAddress());

                                                if(productDataModel.getProductNewOrderCount() <=0){
                                                    productNewOrderCountTextView.setVisibility(View.GONE);
                                                }
                                                locationTextView.setText("Location: "+productDataModel.getLocation());
                                                datePostedTextView.setText("Date: "+productDataModel.getDatePosted());
                                                if(productDataModel.isSold()){
                                                    soldIndicator.setVisibility(View.VISIBLE);
                                                    soldIndicator.setText("Sold");
                                                    soldIndicator.setBackground(new ColorDrawable(context.getResources().getColor(R.color.red_dark,context.getTheme())));
                                                    soldIndicatorButton.setText("Mark as unsold");

                                                }else{
                                                    soldIndicatorButton.setText("Mark as sold");
                                                    soldIndicator.setVisibility(View.INVISIBLE);


                                                }

                                                if(productDataModel.getProductOwnerId().equals(GlobalValue.getCurrentUserId()) || GlobalValue.isBusinessOwner()){
                                                    soldIndicatorButton.setVisibility(View.VISIBLE);
                                                    phoneNumberTextView.setVisibility(View.VISIBLE);
                                                    residentialAddressTextView.setVisibility(View.VISIBLE);
                                                    emailAddressTextView.setVisibility(View.VISIBLE);
                                                }
                                                Picasso.get()
                                                        .load(productDataModel.getImageUrlList().get(0))
                                                        .placeholder(R.drawable.agg_logo)
                                                        .into(icon);
//                                                icon.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View view) {
//                                                        GlobalValue.viewImagePreview(context,icon, productDataModel.getImageUrlList().get(0));
//                                                    }
//                                                });
                                                soldIndicatorButton.setVisibility(View.GONE);
                                                productView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(context, SingleProductActivity.class);
                                                        intent.putExtra(GlobalValue.PRODUCT_ID, productDataModel.getProductId());
                                                        intent.putExtra(GlobalValue.PRODUCT_DATA_MODEL, productDataModel);
                                                        intent.putExtra(GlobalValue.IS_EDITION, false);
                                                        context.startActivity(intent);

                                                    }
                                                });
                                                incrementAdvertViewCount(productId);

                                                advertView.addView(productView);
                                            }
                                        });


                                break;
                            case BUSINESS_PAGE_ADVERT_TYPE:

                                GlobalValue.getFirebaseFirestoreInstance()
                                        .collection(GlobalValue.PAGES)
                                        .document(qualifiedModelIdToDisplay)
                                        .get().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                })
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                String serviceId = documentSnapshot.getId();
                                                String serviceOwnerId = ""+ documentSnapshot.get(GlobalValue.PAGE_OWNER_USER_ID);
                                                String title = ""+ documentSnapshot.get(GlobalValue.PAGE_TITLE);
                                                String description = ""+ documentSnapshot.get(GlobalValue.PAGE_DESCRIPTION);
                                                long totalRequests =  documentSnapshot.get(GlobalValue.TOTAL_PAGE_REQUESTS)!=null? documentSnapshot.getLong(GlobalValue.TOTAL_PAGE_REQUESTS): 0L;
                                                long numberOfNewRequests =  documentSnapshot.get(GlobalValue.TOTAL_NEW_PAGE_REQUESTS)!=null? documentSnapshot.getLong(GlobalValue.TOTAL_NEW_PAGE_REQUESTS): 0L;
                                                String dateAdded =  documentSnapshot.get(GlobalValue.DATE_ADDED_TIME_STAMP)!=null? documentSnapshot.getTimestamp(GlobalValue.DATE_ADDED_TIME_STAMP).toDate()+"": "Undefined";
                                                if(dateAdded.length()>10){
                                                    dateAdded = dateAdded.substring(0,10);
                                                }

                                                boolean isAdvertRequested =  documentSnapshot.get(GlobalValue.IS_ADVERT_REQUESTED)!=null? documentSnapshot.getBoolean(GlobalValue.IS_ADVERT_REQUESTED): false;
                                                boolean isAdvertRunning =  documentSnapshot.get(GlobalValue.IS_ADVERT_RUNNING)!=null? documentSnapshot.getBoolean(GlobalValue.IS_ADVERT_RUNNING): false;

                                                ServiceDataModel serviceDataModel = new ServiceDataModel(serviceId, serviceOwnerId, title, description, dateAdded, totalRequests, numberOfNewRequests);
                                                View businessPageView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.service_item_layout,null,false);

                                                TextView serviceTitleTextView =  businessPageView.findViewById(R.id.serviceTitleTextViewId);
                                                TextView dateAddedTextView =  businessPageView.findViewById(R.id.dateAddedTextViewId);
                                                TextView serviceDescriptionTextView  =  businessPageView.findViewById(R.id.serviceDescriptionTextViewId);
                                                MaterialButton submitRequestActionButton  =  businessPageView.findViewById(R.id.submitRequestActionButtonId);



                                                 serviceTitleTextView.setText(title);
                                                 dateAddedTextView.setText(dateAdded);
                                                 serviceDescriptionTextView.setText(description);
                                                 submitRequestActionButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        Intent intent = new Intent(context, RequestServiceActivity.class);
                                                        intent.putExtra(GlobalValue.PAGE_ID,serviceId);
                                                        intent.putExtra(GlobalValue.PAGE_DATA_MODEL,serviceDataModel);
                                                        intent.putExtra(GlobalValue.PAGE_OWNER_USER_ID,serviceOwnerId);
                                                        intent.putExtra(GlobalValue.PAGE_TITLE,title);

                                                        context.startActivity(intent);
                                                    }
                                                });

                                                businessPageView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        Intent intent = new Intent(context, SingleServiceActivity.class);
                                                        intent.putExtra(GlobalValue.PAGE_ID,serviceId);
                                                        intent.putExtra(GlobalValue.PAGE_DATA_MODEL,serviceDataModel);
                                                        intent.putExtra(GlobalValue.PAGE_OWNER_USER_ID,serviceOwnerId);
                                                        intent.putExtra(GlobalValue.PAGE_TITLE,title);

                                                        context.startActivity(intent);
                                                    }
                                                });
                                                incrementAdvertViewCount(serviceId);

                                                advertView.addView(businessPageView);

                                            }
                                        });
                                break;
                            case PEOPLE_ADVERT_TYPE:
                                GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(qualifiedModelIdToDisplay)
                                        .get()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {


                                                String userId =""+ documentSnapshot.get(GlobalValue.USER_ID);
                                                String userDisplayName =""+ documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME);
                                                String description =""+ documentSnapshot.get(GlobalValue.USER_DESCRIPTION);
                                                String birthdate =""+ documentSnapshot.get(GlobalValue.USER_BIRTH_DATE);
                                                String userCountryOfResidence =""+ documentSnapshot.get(GlobalValue.USER_COUNTRY_OF_RESIDENCE);
                                                String contactEmail =""+ documentSnapshot.get(GlobalValue.USER_CONTACT_EMAIL_ADDRESS);
                                                String webLink = documentSnapshot.get(GlobalValue.USER_PERSONAL_WEBSITE_LINK)!=null? ""+documentSnapshot.get(GlobalValue.USER_PERSONAL_WEBSITE_LINK):"No link";
                                                String contactPhoneNumber =""+ documentSnapshot.get(GlobalValue.USER_CONTACT_PHONE_NUMBER);
                                                String genderType =""+ documentSnapshot.get(GlobalValue.USER_GENDER_TYPE);
                                                String userProfilePhotoDownloadUrl =""+ documentSnapshot.get(GlobalValue.USER_COVER_PHOTO_DOWNLOAD_URL);
                                                String joined_date = documentSnapshot.get(GlobalValue.USER_PROFILE_DATE_CREATED_TIME_STAMP)!=null ? documentSnapshot.getTimestamp(GlobalValue.USER_PROFILE_DATE_CREATED_TIME_STAMP).toDate()+"" :"Undefined";
                                                if(joined_date.length()>10){
                                                    joined_date = joined_date.substring(0,10);
                                                }

                                                boolean isUserBlocked = false;
                                                boolean isUserProfilePhotoIncluded = false;
                                                if(documentSnapshot.get(GlobalValue.IS_USER_BLOCKED) != null){
                                                    isUserBlocked =documentSnapshot.getBoolean(GlobalValue.IS_USER_BLOCKED);

                                                }
                                                if(documentSnapshot.get(GlobalValue.IS_USER_PROFILE_PHOTO_INCLUDED) != null){
                                                    isUserProfilePhotoIncluded = documentSnapshot.getBoolean(GlobalValue.IS_USER_PROFILE_PHOTO_INCLUDED);

                                                }
                                                View userView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.service_item_layout,null,false);


                                                 TextView userNameTextView  =  userView.findViewById(R.id.title);
                                                 TextView dateCreatedTextView = (TextView) userView.findViewById(R.id.dateCreated);
                                                //        public TextView description = userView.findViewById(R.id.description);
                                                 RoundedImageView icon = userView.findViewById(R.id.icon);
                                                 ImageButton moreActionButton  = userView.findViewById(R.id.moreActionButtonId);
                                                 ImageButton startChatActionButton = userView.findViewById(R.id.startChatActionButtonId);





                                                 userNameTextView.setText(userDisplayName);
                                                 dateCreatedTextView.setText(joined_date);
                                                //            holder.description.setText(userDataModels.getDescription());

                                                Glide.with(context)
                                                        .load(userProfilePhotoDownloadUrl)
                                                        .placeholder(R.drawable.default_profile)
                                                        .centerCrop()
                                                        .into(icon);
//        holder.moreActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LEBottomSheetDialog leBottomSheetDialog = new LEBottomSheetDialog(context);
//                leBottomSheetDialog.addOptionItem("Block User", R.drawable.ic_baseline_error_outline_24, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        leBottomSheetDialog.hide();
//                        Toast.makeText(context,"Blocking",Toast.LENGTH_SHORT).show();
//                        int position = userDataModelsList.indexOf(userDataModels);
//                        userDataModelsList.remove(userDataModels);
//                        UsersRCVAdapter.this.notifyItemRemoved(position);
//                        GlobalConfig.block(GlobalConfig.ACTIVITY_LOG_USER_BLOCK_USER_TYPE_KEY, userDataModels.getUserId(), null, null, new GlobalConfig.ActionCallback() {
//                            @Override
//                            public void onSuccess() {
//
//                            }
//
//                            @Override
//                            public void onFailed(String errorMessage) {
//
//                            }
//                        });
//                    }
//                }, 0);
//                leBottomSheetDialog.addOptionItem("Report User", R.drawable.ic_baseline_error_outline_24, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        leBottomSheetDialog.hide();
//                        Toast.makeText(context,"reporting",Toast.LENGTH_SHORT).show();
//                        int position = userDataModelsList.indexOf(userDataModels);
//                        userDataModelsList.remove(userDataModels);
//                        UsersRCVAdapter.this.notifyItemRemoved(position);
//                        GlobalConfig.report(GlobalConfig.ACTIVITY_LOG_USER_REPORT_USER_TYPE_KEY, userDataModels.getUserId(), null, null, new GlobalConfig.ActionCallback() {
//                            @Override
//                            public void onSuccess() {
//
//                            }
//
//                            @Override
//                            public void onFailed(String errorMessage) {
//
//                            }
//                        });
//                    }
//                }, 0);
//                leBottomSheetDialog.render().show();
//            }
//        });


                                                startChatActionButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        context.startActivity(GlobalValue.goToChatRoom(context, userId, userDisplayName, icon));

                                                    }
                                                });

                                                userView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        context.startActivity(GlobalValue.getHostActivityIntent(context,null,GlobalValue.USER_PROFILE_FRAGMENT_TYPE,userId));

                                                    }
                                                });
                                                incrementAdvertViewCount(userId);

                                                advertView.addView(userView);

                                            }
                                        });

                                break;
                        }


                    }
                });


        return advertView;

    }

    public static ArrayList<String> getPageCategory(){
        ArrayList<String> categoryList = new ArrayList<String>();
        categoryList.add(GlobalValue.PROVISION_SHOP);
        categoryList.add(GlobalValue.SECONDARY_SCHOOL);
        categoryList.add(GlobalValue.PRIMARY_SCHOOL);
        categoryList.add(GlobalValue.HOTEL);
        categoryList.add(GlobalValue.RESTAURANT);
        categoryList.add(GlobalValue.HIGHER_INSTITUTION);
        categoryList.add(GlobalValue.CYBER_CAFE);
        categoryList.add(GlobalValue.COMPUTER_SHOP);

        return categoryList;
    }

}
