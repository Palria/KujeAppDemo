
package com.govance.businessprojectconfiguration;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.govance.businessprojectconfiguration.adapters.ProductOrderRcvAdapter;
import com.govance.businessprojectconfiguration.models.ProductOrderDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AllOrdersFragment extends Fragment {



    public AllOrdersFragment() {
        // Required empty public constructor
    }
    OrderCallback orderCallback;
    ProductOrderRcvAdapter productOrderRcvAdapter;
    ArrayList<ProductOrderDataModel> productOrderDataModelArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    ShimmerFrameLayout shimmerLayout, progressIndicatorShimmerLayout;
    FirebaseFirestore firebaseFirestore;
    String userId;
    LinearLayout containerLinearLayout;
    boolean isLoadingMoreOrders = false;
    boolean isFirstLoad = true;
    boolean isSingleProduct = false;
    boolean isSingleCustomer = false;
    String productId = "";
    String customerId = "";
    DocumentSnapshot lastRetrievedOrdersSnapshot = null;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
firebaseFirestore = FirebaseFirestore.getInstance();
userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
if(getArguments() != null){
    productId = getArguments().getString(GlobalValue.PRODUCT_ID,"");
    customerId = getArguments().getString(GlobalValue.CUSTOMER_ID,"");
    isSingleProduct = getArguments().getBoolean(GlobalValue.IS_SINGLE_PRODUCT,false);
    isSingleCustomer = getArguments().getBoolean(GlobalValue.IS_SINGLE_CUSTOMER,false);
}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_all_orders, container, false);
        initUI(parentView);
        initRecyclerView();

        orderCallback = new OrderCallback() {
            @Override
            public void onSuccess(ProductOrderDataModel productOrderDataModel) {
            productOrderDataModelArrayList.add(productOrderDataModel);
            productOrderRcvAdapter.notifyItemChanged(productOrderDataModelArrayList.size());
            }

            @Override
            public void onFailed(String errorMessage) {

            }
        };
getAllOrders();

        if(getContext().getClass().equals(SingleProductActivity.class)){
            recyclerView.setPadding(2,2,2,200);
        }
        return parentView;
    }
  private  void initUI(View parentView){
        recyclerView = parentView.findViewById(R.id.productOrderRecyclerViewId);
      containerLinearLayout = parentView.findViewById(R.id.containerLinearLayoutId);
      shimmerLayout = parentView.findViewById(R.id.ordersShimmerLayoutId);

  }
  private void initRecyclerView(){
      productOrderRcvAdapter = new ProductOrderRcvAdapter(productOrderDataModelArrayList,getContext(),isSingleProduct);
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
      recyclerView.setLayoutManager(linearLayoutManager);
      recyclerView.setAdapter(productOrderRcvAdapter);

     /* productOrderDataModelArrayList.add(new ProductOrderDataModel(
              productId,
              "productName",
              "productImageDownloadUrl",
              "orderId",
              "customerId",
              "dateOrdered2",
              "orderDescription",
              "customerContactPhoneNumber",
              "customerContactEmail",
              "customerContactAddress",
              "customerContactLocation",
              false
      ));
      */

      productOrderRcvAdapter.notifyItemChanged(productOrderDataModelArrayList.size());
  }
    private void getAllOrders() {
        Query orderQuery = firebaseFirestore.collection(GlobalValue.ALL_ORDERS).limit(20L);

        if (!isLoadingMoreOrders) {

            if (isFirstLoad) {

                productOrderDataModelArrayList.clear();
                productOrderRcvAdapter.notifyDataSetChanged();
                shimmerLayout.startShimmer();
                shimmerLayout.setVisibility(View.VISIBLE);
            } else {
                orderQuery = firebaseFirestore.collection(GlobalValue.ALL_ORDERS).limit(20L).startAfter(lastRetrievedOrdersSnapshot);
                progressIndicatorShimmerLayout = GlobalValue.showShimmerLayout(getContext(), containerLinearLayout);

            }
            if (isSingleProduct && isSingleCustomer) {
                if (isFirstLoad) {
                    orderQuery = firebaseFirestore.collection(GlobalValue.ALL_ORDERS).whereEqualTo(GlobalValue.PRODUCT_ID, productId).whereEqualTo(GlobalValue.CUSTOMER_ID, customerId).limit(20);
                }else {
                    orderQuery = firebaseFirestore.collection(GlobalValue.ALL_ORDERS).whereEqualTo(GlobalValue.PRODUCT_ID, productId).whereEqualTo(GlobalValue.CUSTOMER_ID, customerId).startAfter(lastRetrievedOrdersSnapshot).limit(20);
                }
            } else if (isSingleProduct) {
                if (isFirstLoad) {
                    orderQuery = firebaseFirestore.collection(GlobalValue.ALL_ORDERS).whereEqualTo(GlobalValue.PRODUCT_ID, productId).limit(20);
                }else {
                    orderQuery = firebaseFirestore.collection(GlobalValue.ALL_ORDERS).whereEqualTo(GlobalValue.PRODUCT_ID, productId).startAfter(lastRetrievedOrdersSnapshot).limit(20);
                }
            } else if (isSingleCustomer) {
                if (isFirstLoad) {
                    orderQuery = firebaseFirestore.collection(GlobalValue.ALL_ORDERS).whereEqualTo(GlobalValue.CUSTOMER_ID, customerId).limit(20);
                }else {
                    orderQuery = firebaseFirestore.collection(GlobalValue.ALL_ORDERS).whereEqualTo(GlobalValue.CUSTOMER_ID, customerId).startAfter(lastRetrievedOrdersSnapshot).limit(20);
                }
            }

            isLoadingMoreOrders = true;

            orderQuery.get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    isLoadingMoreOrders = false;

                }
            }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String orderId = documentSnapshot.getId();

                        String dateOrdered = documentSnapshot.get(GlobalValue.DATE_ORDERED_TIME_STAMP) != null ? documentSnapshot.getTimestamp(GlobalValue.DATE_ORDERED_TIME_STAMP).toDate() + "" : "Undefined";
                        if (dateOrdered.length() > 10) {
                            dateOrdered = dateOrdered.substring(0, 10);
                        }
                        final String dateOrdered2 = dateOrdered;
                        String productId = "" + documentSnapshot.get(GlobalValue.PRODUCT_ID);
                        String productOwnerId = "" + documentSnapshot.get(GlobalValue.PRODUCT_OWNER_USER_ID);
                        String productName = "" + documentSnapshot.get(GlobalValue.PRODUCT_TITLE);
                        String productImageDownloadUrl = "" + documentSnapshot.get(GlobalValue.PRODUCT_IMAGE_DOWNLOAD_URL);

                        String customerId = "" + documentSnapshot.get(GlobalValue.CUSTOMER_ID);
                        String customerContactPhoneNumber = "" + documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_PHONE_NUMBER);
                        String customerContactEmail = "" + documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_EMAIL);
                        String customerContactAddress = "" + documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_ADDRESS);
                        String customerContactLocation = "" + documentSnapshot.get(GlobalValue.CUSTOMER_CONTACT_LOCATION);
                        String orderDescription = "" + documentSnapshot.get(GlobalValue.ORDER_DESCRIPTION);
                        boolean isResolved = documentSnapshot.get(GlobalValue.IS_ORDER_RESOLVED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_ORDER_RESOLVED) : false;
                        boolean isOwnerSeen = documentSnapshot.get(GlobalValue.IS_OWNER_SEEN) != null ? documentSnapshot.getBoolean(GlobalValue.IS_OWNER_SEEN) : false;

                        orderCallback.onSuccess(new ProductOrderDataModel(
                                productId,
                                productOwnerId,
                                productName,
                                productImageDownloadUrl,
                                orderId,
                                customerId,
                                dateOrdered2,
                                orderDescription,
                                customerContactPhoneNumber,
                                customerContactEmail,
                                customerContactAddress,
                                customerContactLocation,
                                isResolved,
                                isOwnerSeen
                        ));


//                    firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(GlobalValue.getBusinessAdminId()).collection(GlobalValue.PAGES).document(GlobalValue.getCurrentBusinessId()).collection(GlobalValue.PRODUCTS).document(productId).collection(GlobalValue.PRODUCT_PROFILE).document(productId).get().addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                            String productImageImageDownloadUrl = ""+documentSnapshot.get(GlobalValue.IMAGE_DOWNLOAD_URL);
//                            String productName  = ""+documentSnapshot.get(GlobalValue.PRODUCT_TITLE);
//                            String productDescription = ""+documentSnapshot.get(GlobalValue.PRODUCT_DESCRIPTION);
//
////                            displayOrderView(getContext(),
////                                    containerLinearLayout,
////                                     productImageImageDownloadUrl,
////                                     productName,
////                                     productId,
////                                     dateOrdered2,
////                                     productDescription,
////                                     customerName,
////                                     customerContactPhoneNumber,
////                                     customerContactEmail,
////                                     customerContactAddress,
////                                     customerContactLocation,
////                                     orderDescription
////                            );
//                        }
//                    });
                    }

                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.INVISIBLE);
                    GlobalValue.removeShimmerLayout(containerLinearLayout, progressIndicatorShimmerLayout);
                    if (queryDocumentSnapshots.size() == 0) {
                        if (isFirstLoad) {

                        }
                    } else {
                        lastRetrievedOrdersSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    }
                    isFirstLoad = false;
                    isLoadingMoreOrders = false;

                }
            });
        }
    }

    private void displayOrderView(Context context,
                ViewGroup viewGroup,
                String productImageImageDownloadUrl,
                String productName,
                String productId,
                String dateOrdered,
                String productDescription,
                String customerName,
                String customerContactPhoneNumber,
                String customerContactEmail,
                String customerContactAddress,
                String customerContactLocation,
                String orderDescription
    ) {

            if (context != null) {
                View itemView = LayoutInflater.from(context).inflate(R.layout.single_product_order_layout_holder_view, viewGroup, false);


                ImageView firstPostImageView = itemView.findViewById(R.id.firstProductImageViewId);

                ImageView secondPostImageView = itemView.findViewById(R.id.secondProductImageViewId);


//                StyledPlayerView styledPlayerView = itemView.findViewById(R.id.styledPlayerViewId);


//            ImageView totalNumberOfViewsImageView = itemView.findViewById(R.id.totalNumberOfViewsImageViewId);
//
//            ImageView totalNumberOfCommentsImageView = itemView.findViewById(R.id.totalNumberOfCommentsImageViewId);
//
//            ImageView totalNumberOfLikesImageView = itemView.findViewById(R.id.totalNumberOfLikesImageViewId);
//
//            ImageView totalNumberOfSharesImageView = itemView.findViewById(R.id.totalNumberOfSharesImageViewId);

                TextView productNameTextView = itemView.findViewById(R.id.productDisplayNameTextViewId);
                TextView customerNameTextView = itemView.findViewById(R.id.customerContactNameTextViewId);

                TextView dateOrderedTextView = itemView.findViewById(R.id.dateOrderedTextViewId);
                TextView customerContactEmailTextView = itemView.findViewById(R.id.customerContactEmailTextViewId);
                TextView customerContactPhoneNumberTextView = itemView.findViewById(R.id.customerContactPhoneNumberTextViewId);
                TextView customerContactAddressTextView = itemView.findViewById(R.id.customerContactAddressTextViewId);
                TextView customerContactLocationTextView = itemView.findViewById(R.id.customerContactLocationTextViewId);

                TextView orderDescriptionTextView = itemView.findViewById(R.id.orderDescriptionTextViewId);
//
//            TextView totalNumberOfViewsTextView = itemView.findViewById(R.id.totalNumberOfViewsTextViewId);
//            TextView totalNumberOfCommentsTextView = itemView.findViewById(R.id.totalNumberOfCommentsTextViewId);
//            TextView totalNumberOfLikesTextView = itemView.findViewById(R.id.totalNumberOfLikesTextViewId);
//            TextView totalNumberOfSharesTextView = itemView.findViewById(R.id.totalNumberOfSharesTextViewId);
//            TextView postIdHolderTextView = itemView.findViewById(R.id.postIdHolderTextViewId);
//
//
//            View addCommentIncludeLayoutView = itemView.findViewById(R.id.addCommentIncludeLayoutViewId);

//
//            EditText addCommentEditText = itemView.findViewById(R.id.addCommentEditTextId);
//            MaterialButton confirmAddCommentActionButton = itemView.findViewById(R.id.confirmAddCommentActionButtonId);
//            confirmAddCommentActionButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    addCommentIncludeLayoutView.setVisibility(View.GONE);
//
////                addCommentToPost(getContext(),postId,addCommentEditText);
//                    GlobalValue.addCommentToPost(context, firebaseFirestore, postId, addCommentEditText.getText().toString(), userId, pageId, userId, true, new GlobalValue.ActionCallback() {
//                        @Override
//                        public void onActionSuccess() {
//
//                        }
//
//                        @Override
//                        public void onActionFailure(@NonNull Exception e) {
//
//                        }
//                    });
//                    addCommentEditText.setText("");
//                    GlobalValue.hideKeyboard(getContext());
//
////                    addCommentToMainUserPost(getContext(),postOwnerUserId,postId);
//
//                }
//            });
//            MaterialButton cancelCommentActionButton = itemView.findViewById(R.id.cancelCommentActionButtonId);
//            cancelCommentActionButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    addCommentIncludeLayoutView.setVisibility(View.GONE);
//                    GlobalValue.hideKeyboard(getContext());
//
//
//                }
//            });
//
//
//            LinearLayout allThreeCommentsLinearLayout = itemView.findViewById(R.id.allThreeCommentsLinearLayoutId);
//

                if (!productImageImageDownloadUrl.equals("null")) {
                    Picasso.get().load(productImageImageDownloadUrl).error(R.drawable.ic_baseline_image_24).into(firstPostImageView);

                }
//            if(postProfileVideoDownloadUrl.equals("null") && !thirdPostProfileImageDownloadUrl.equals("null")) {
//                styledPlayerView.setVisibility(View.GONE);
//                thirdPostImageView.setVisibility(View.VISIBLE);
//                Picasso.get().load(thirdPostProfileImageDownloadUrl).error(R.drawable.ic_baseline_image_24).into(thirdPostImageView);
//
//            }if (!postProfileVideoDownloadUrl.equals("null") || thirdPostProfileImageDownloadUrl.equals("null")) {
//                thirdPostImageView.setVisibility(View.GONE);
//            }

//                if(!postProfileVideoDownloadUrl.equals("null")){
//                    styledPlayerView.setVisibility(View.VISIBLE);
//                    GlobalValue.displayExoplayerVideo(context,styledPlayerView,activeExoPlayerArrayList, Uri.parse(postProfileVideoDownloadUrl));
//
//                }else{
//                    styledPlayerView.setVisibility(View.GONE);
//
//                }
            productNameTextView.setText(productName);
            customerNameTextView.setText(customerName);
            dateOrderedTextView.setText(GlobalValue.getFormattedDate(dateOrdered));
                customerContactEmailTextView.setText(customerContactEmail);
                customerContactPhoneNumberTextView.setText(customerContactPhoneNumber);
                customerContactAddressTextView.setText(customerContactAddress);
                customerContactLocationTextView.setText(customerContactLocation);
                orderDescriptionTextView.setText(orderDescription);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent intent = new Intent(getContext(), SingleProductActivity.class);
//                        intent.putExtra("POST_ID", postId);
//                        intent.putExtra("PAGE_POSTER_ID", pageId);
//                        intent.putExtra("POST_OWNER_DISPLAY_NAME", pageName);
//
//                        startActivity(intent);
                    }
                });



                viewGroup.addView(itemView);
            }

        }

    interface OrderCallback{
        void onSuccess(ProductOrderDataModel orderDataModel);
        void onFailed(String errorMessage);
    }

}