package com.example.projectqizz.DB;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectqizz.Model.CategoryModel;
import com.example.projectqizz.Model.ProfileModel;
import com.example.projectqizz.Model.QuestionModel;
import com.example.projectqizz.Model.RankModel;
import com.example.projectqizz.Model.TestModel;
import com.example.projectqizz.MyCompleteListener;
import com.example.projectqizz.MyProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbQuery{
    public static FirebaseFirestore g_firestore;
    public static List<CategoryModel> g_catList = new ArrayList<>();
    public static int g_selected_cat_index = 0;
    public static List<TestModel> g_testList = new ArrayList<>();
    public static int g_selected_test_index = 0;
    public static ProfileModel myProfile = new ProfileModel("NA",null,false,null,0);
    public static List<QuestionModel> g_quesList = new ArrayList<>();
    public static List<RankModel> g_usersList = new ArrayList<>();
    public static int g_usersCount = 0;
    public static boolean isMeOnTopList = false;
    public static RankModel myPerformance = new RankModel("NULL",0,0);
    public static final int NOT_VISITED = 0;
    public static final int UNANSWERED =1 ;
    public static final int ANSWERED = 2;
    public static final int REVIEW = 3;
    public static List<String> g_bmIdList = new ArrayList<>();
    public static List<QuestionModel> g_bookmarksList = new ArrayList<>();
    static int tmp;

    public static void loadBookmarks(MyCompleteListener myCompleteListener){
        g_bookmarksList.clear();
        tmp = 0 ;
        if(g_bmIdList.size() == 0 ){
            myCompleteListener.onSucces();
        }
        for (int i = 0; i < g_bmIdList.size() ; i++){
            String docId = g_bmIdList.get(i);

            g_firestore.collection("Questions").document(docId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            boolean isBookmarked = false;
//                            if(g_bmIdList.contains(documentSnapshot.getId())){
//                                isBookmarked=true;
//                            }
                            if (documentSnapshot.exists()){
                                g_bookmarksList.add(new QuestionModel(
                                        documentSnapshot.getId(),
                                        documentSnapshot.getString("QUESTION"),
                                        documentSnapshot.getString("A"),
                                        documentSnapshot.getString("B"),
                                        documentSnapshot.getString("C"),
                                        documentSnapshot.getString("D"),
                                        documentSnapshot.getLong("ANSWER").intValue(),
                                        0,
                                        -1,
                                        false
                                ));
                            }
                            tmp++;
                            if(tmp == g_bmIdList.size()){
                                myCompleteListener.onSucces();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            myCompleteListener.onFailure();
                        }
                    });

        }
    }

    public static void loadBmIds(MyCompleteListener myCompleteListener){
        g_bmIdList.clear();

        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("BOOKMARKS")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int count = myProfile.getBookMarksCount();
                        for (int i = 0 ; i < count ; i ++){
                            String bmId = documentSnapshot.getString("BM"+String.valueOf(i+1)+"_ID");
                            g_bmIdList.add(bmId);
                        }
                        myCompleteListener.onSucces();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });

    }

    public static void getTopUsers(MyCompleteListener myCompleteListener){
        g_usersList.clear();

        String myUID = FirebaseAuth.getInstance().getUid();

        g_firestore.collection("USERS")
                .whereGreaterThan("TOTAL_SCORE",0)
                .orderBy("TOTAL_SCORE", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int rank = 1;
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            g_usersList.add(new RankModel(
                                    doc.getString("NAME"),
                                    doc.getLong("TOTAL_SCORE").intValue(),
                                    rank
                            ));
                            if(myUID.compareTo(doc.getId()) == 0){
                                isMeOnTopList = true;
                                myPerformance.setRank(rank);
                            }
                            rank++;
                        }
                        myCompleteListener.onSucces();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }
    public static void getUserCount(MyCompleteListener myCompleteListener){
        g_firestore.collection("USERS").document("TOTAL_USERS").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        g_usersCount = documentSnapshot.getLong("COUNT").intValue();
                        myCompleteListener.onSucces();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }
    public static void saveProfileData(String name, String phone, MyCompleteListener myCompleteListener){
        Map<String,Object> profileData = new ArrayMap<>();
        profileData.put("NAME",name);
        if(phone != null){
            profileData.put("PHONE",phone);
        }

        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).update(profileData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myProfile.setName(name);
                        if(phone!=null){
                            myProfile.setPhone(phone);
                        }
                        myCompleteListener.onSucces();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }
    public static void loadMyScore(MyCompleteListener myCompleteListener){
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_SCORES")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        for (int i = 0 ;i < g_testList.size(); i++){
                            int top = 0;
                            if(documentSnapshot.get(g_testList.get(i).getTestID()) != null){
                                top = documentSnapshot.getLong(g_testList.get(i).getTestID()).intValue();
                            }
                            g_testList.get(i).setTopScore(top);
                        }
                        myCompleteListener.onSucces();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }
    public static void saveResult(int score, MyCompleteListener myCompleteListener) {
        WriteBatch batch = g_firestore.batch();

        //BookMarks
        Map<String,Object> bmData = new ArrayMap<>();
        for(int i = 0 ; i < g_bmIdList.size(); i++){
            bmData.put("BM"+String.valueOf(i+1)+"_ID",g_bmIdList.get(i));
        }
        DocumentReference bmDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("BOOKMARKS");
        batch.set(bmDoc,bmData);

        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid());

        batch.update(userDoc,"BOOKMARKS",g_bmIdList.size());

        if (score > g_testList.get(g_selected_test_index).getTopScore()) {
            DocumentReference scoreDoc = userDoc.collection("USER_DATA").document("MY_SCORES");
            Map<String, Object> testData = new ArrayMap<>();
            testData.put(g_testList.get(g_selected_test_index).getTestID(), score);
            batch.set(scoreDoc, testData, SetOptions.merge());
        }

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if(score > g_testList.get(g_selected_test_index).getTopScore()){
                            g_testList.get(g_selected_test_index).setTopScore(score);
                        }
                        userDoc.collection("USER_DATA").document("MY_SCORES").get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        int top = 0;
                                        for (int i = 0; i < g_testList.size(); i++) {
                                            if (documentSnapshot.get(g_testList.get(i).getTestID()) != null) {
                                                top += documentSnapshot.getLong(g_testList.get(i).getTestID()).intValue();
                                            }
                                        }
                                        myPerformance.setScore(top);
                                        userDoc.update("TOTAL_SCORE", top)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        myCompleteListener.onSucces();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        myCompleteListener.onFailure();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        myCompleteListener.onFailure();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }

    public static void loadQuestions(MyCompleteListener myCompleteListener){
        g_quesList.clear();
        g_firestore.collection("Questions")
                .whereEqualTo("CATEGORY",g_catList.get(g_selected_cat_index).getDocId())
                .whereEqualTo("TEST",g_testList.get(g_selected_test_index).getTestID())
                .get()// lấy tất cả các doccument thỏa mãn 2 điều kiện where trên
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc : queryDocumentSnapshots){
                            boolean isBookmarked = false;
                            if(g_bmIdList.contains(doc.getId())){
                                isBookmarked=true;
                            }
                            g_quesList.add(new QuestionModel(
                                    doc.getId(),
                                    doc.getString("QUESTION"),
                                    doc.getString("A"),
                                    doc.getString("B"),
                                    doc.getString("C"),
                                    doc.getString("D"),
                                    doc.getLong("ANSWER").intValue(),
                                    -1,
                                    NOT_VISITED,
                                    isBookmarked
                            ));
                        }
                        myCompleteListener.onSucces();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }
    public static void getUserData(MyCompleteListener myCompleteListener){
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        myProfile.setName(documentSnapshot.getString("NAME"));
                        myProfile.setEmail(documentSnapshot.getString("EMAIL_ID"));
                        myProfile.setAdmin(documentSnapshot.getBoolean("IsAdmin"));
                        if(documentSnapshot.getString("PHONE") != null){
                            myProfile.setPhone(documentSnapshot.getString("PHONE"));
                        }
                        if(documentSnapshot.getLong("BOOKMARKS") != null){
                            myProfile.setBookMarksCount(documentSnapshot.getLong("BOOKMARKS").intValue());
                        }
                        myPerformance.setScore(documentSnapshot.getLong("TOTAL_SCORE").intValue());
                        myPerformance.setName(documentSnapshot.getString("NAME"));
                        myCompleteListener.onSucces();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }

    public static void createUserData(String email, String name, MyCompleteListener myCompleteListener){
        Map<String,Object> userData = new ArrayMap<>();
        userData.put("EMAIL_ID",email);
        userData.put("NAME",name);
        userData.put("TOTAL_SCORE",0);
        userData.put("IsAdmin",false);
        userData.put("BOOKMARKS",0);

        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        WriteBatch batch = g_firestore.batch();

        batch.set(userDoc,userData);//thêm vào database

        DocumentReference countDoc = g_firestore.collection("USERS").document("TOTAL_USERS");
        batch.update(countDoc,"COUNT", FieldValue.increment(1));//sau khi thêm cập nhập người dùng tăng lên 1

        batch.commit()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        myCompleteListener.onSucces();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });

    }

    public static void loadCategories(MyCompleteListener myCompleteListener)//lấy tất cả category
    {
        g_catList.clear();

        g_firestore.collection("QUIZ").get() //lấy ta all doccument
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, QueryDocumentSnapshot> docList = new ArrayMap<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots)//đã lấy ra tất cả các doc
                        {
                            docList.put(doc.getId(),doc);
                        }
                        QueryDocumentSnapshot catListDooc = docList.get("Categories");

                        long catCount = catListDooc.getLong("COUNT");
                        for (int i = 1;i <= catCount;i++){
                            String catID = catListDooc.getString("CAT"+String.valueOf(i)+"_ID");
                            QueryDocumentSnapshot catDoc = docList.get(catID);

                            int noOfTest = catDoc.getLong("NO_OF_TESTS").intValue();
                            String catName = catDoc.getString("NAME");

                            g_catList.add(new CategoryModel(catID,catName,noOfTest));

                        }

                        myCompleteListener.onSucces();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }

    public  static  void loadTestData(MyCompleteListener myCompleteListener){
        g_testList.clear();

        g_firestore.collection("QUIZ").document(g_catList.get(g_selected_cat_index).getDocId())
                .collection("TESTS_LIST").document("TESTS_INFO").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        int noOfTests = g_catList.get(g_selected_cat_index).getNoOfTests();
                        for(int i = 1 ;i <= noOfTests;i++){
                            g_testList.add(new TestModel(
                                    documentSnapshot.getString("TEST"+String.valueOf(i)+"_ID"),
                                    0,
                                    documentSnapshot.getLong("TEST"+String.valueOf(i)+"_TIME").intValue(),
                                    documentSnapshot.getString("TEST"+String.valueOf(i)+"_NAME")
                            ));
                        }


                        myCompleteListener.onSucces();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });

    }
    public static void loadData(MyCompleteListener myCompleteListener){
        loadCategories(new MyCompleteListener() {
            @Override
            public void onSucces() {

                getUserData(new MyCompleteListener() {
                    @Override
                    public void onSucces() {
                        getUserCount(new MyCompleteListener()
                        {
                            @Override
                            public void onSucces() {
                                loadBmIds(myCompleteListener);
                            }

                            @Override
                            public void onFailure() {
                                myCompleteListener.onFailure();
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        myCompleteListener.onFailure();
                    }
                });
            }

            @Override
            public void onFailure() {
                myCompleteListener.onFailure();
            }
        });
    }

}
