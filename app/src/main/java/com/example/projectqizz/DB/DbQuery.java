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

    public static void createQuestion(String nameQues,String A,String B,String C,String D,int answer,MyCompleteListener myCompleteListener)
    {//Hàm này xử lý thêm câu hỏi vào database - Nguyễn Quang Huy
        Map<String,Object> questionData = new ArrayMap<>();
        questionData.put("QUESTION",nameQues);
        questionData.put("A",A);
        questionData.put("B",B);
        questionData.put("C",C);
        questionData.put("D",D);
        questionData.put("ANSWER",answer);
        questionData.put("CATEGORY",g_catList.get(g_selected_cat_index).getDocId());
        questionData.put("TEST",g_testList.get(g_selected_test_index).getTestID());

        DocumentReference quizDoc = g_firestore.collection("Questions").document();

        WriteBatch batch = g_firestore.batch();

        batch.set(quizDoc,questionData);//thêm vào database

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        g_quesList.add(new QuestionModel(
                                quizDoc.getId(),
                                nameQues,
                                A,
                                B,
                                C,
                                D,
                                answer,
                                -1,
                                NOT_VISITED,
                                false
                        ));
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
    public static void createTest(String name,int time,MyCompleteListener myCompleteListener)
    {//Hàm này xử lý thêm bài kiểm tra vào database - Nguyễn Quang Huy
        int noTest = g_catList.get(g_selected_cat_index).getNoOfTests()+1;
        Map<String,Object> testData = new ArrayMap<>();
        testData.put("TEST"+String.valueOf(noTest)+"_ID",g_catList.get(g_selected_cat_index).getDocId()+String.valueOf(noTest));
        testData.put("TEST"+String.valueOf(noTest)+"_NAME",name);
        testData.put("TEST"+String.valueOf(noTest)+"_TIME",time);

        g_firestore.collection("QUIZ").document(g_catList.get(g_selected_cat_index).getDocId())
                .collection("TESTS_LIST").document("TESTS_INFO").set(testData,SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DocumentReference quizDoc = g_firestore.collection("QUIZ").document(g_catList.get(g_selected_cat_index).getDocId());
                        quizDoc.update("NO_OF_TESTS", noTest)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        g_testList.add(new TestModel(g_catList.get(g_selected_cat_index).getDocId()+String.valueOf(noTest),
                                                0,time,name));
                                        g_catList.get(g_selected_cat_index).setNoOfTests(noTest);
                                        myCompleteListener.onSucces();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//                                        Log.e("tag2", "Error: " + e.getMessage(), e);
                                        myCompleteListener.onFailure();
                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Log.e("tag1", "Error: " + e.getMessage(), e);
                        myCompleteListener.onFailure();

                    }
                });

    }
    public static void createCategory(String name,MyCompleteListener myCompleteListener)
    {//Hàm này xử lý thêm danh mục vào database - Nguyễn Quang Huy
        Map<String,Object> categoryData = new ArrayMap<>();
        categoryData.put("NAME",name);
        categoryData.put("NO_OF_TESTS",0);


        DocumentReference quizDoc = g_firestore.collection("QUIZ").document();

        WriteBatch batch = g_firestore.batch();

        batch.set(quizDoc,categoryData);//thêm vào database



        batch.commit()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String documentId = quizDoc.getId();
                        g_catList.add(new CategoryModel(documentId,name,0));
                        quizDoc.update("CAT_ID",documentId);
                        WriteBatch batch = g_firestore.batch();
                        DocumentReference countCategory = g_firestore.collection("QUIZ").document("Categories");
                        batch.update(countCategory,"COUNT", FieldValue.increment(1));
                        batch.update(countCategory,"CAT"+String.valueOf(g_catList.size())+"_ID",documentId);
                        batch.commit()
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
    public static void updateQuestion(int position,String nameQues,String A,String B,String C,String D,int answer,MyCompleteListener myCompleteListener)
    {//Hàm này xử lý cập nhật câu hỏi vào database - Nguyễn Quang Huy
        Map<String,Object> questionData = new ArrayMap<>();
        questionData.put("QUESTION",nameQues);
        questionData.put("A",A);
        questionData.put("B",B);
        questionData.put("C",C);
        questionData.put("D",D);
        questionData.put("ANSWER",answer);
        g_firestore.collection("Questions").document(g_quesList.get(position).getqID()).update(questionData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        g_quesList.get(position).setQuestion(nameQues);
                        g_quesList.get(position).setOptionA(A);
                        g_quesList.get(position).setOptionB(B);
                        g_quesList.get(position).setOptionC(C);
                        g_quesList.get(position).setOptionD(D);
                        g_quesList.get(position).setCorrectAns(answer);
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
    public static void updateTest(int position,String name,int time,MyCompleteListener myCompleteListener)
    {//Hàm này xử lý cập nhật bài kiểm tra vào database - Nguyễn Quang Huy
        Map<String,Object> categoryData = new ArrayMap<>();
        categoryData.put("TEST"+String.valueOf(position+1)+"_NAME",name);
        categoryData.put("TEST"+String.valueOf(position+1)+"_TIME",time);

        g_firestore.collection("QUIZ").document(g_catList.get(g_selected_cat_index).getDocId()).
                collection("TESTS_LIST").document("TESTS_INFO").update(categoryData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        g_testList.get(position).setName(name);
                        g_testList.get(position).setTime(time);
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
    public static void updateCategory(int postion,String name,MyCompleteListener myCompleteListener)
    {//Hàm này xử lý cập nhật danh mục vào database - Nguyễn Quang Huy
        Map<String,Object> categoryData = new ArrayMap<>();
        categoryData.put("NAME",name);

        g_firestore.collection("QUIZ").document(g_catList.get(postion).getDocId()).update(categoryData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        g_catList.get(postion).setName(name);
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

    public static void loadBookmarks(MyCompleteListener myCompleteListener)
    {//Hàm này xử lý tải danh sách câu hỏi đã lưu của người dùng - Nguyễn Quang Huy
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

    public static void loadBmIds(MyCompleteListener myCompleteListener)
    {//Hàm này xử lý tải tất cả ID câu hỏi mà người dùng đã lưu - Nguyễn Quang Huy
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

    public static void getTopUsers(MyCompleteListener myCompleteListener)
    {//Hàm này xử lý tải danh sách điểm của người dùng - Bành Viết Hùng
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
    public static void getUserCount(MyCompleteListener myCompleteListener)
    {//Hàm này xử lý đếm số lượng người dùng - Bành Viết Hùng
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
    public static void saveProfileData(String name, String phone, MyCompleteListener myCompleteListener)
    {//Hàm này xử lý cập nhập thông tin người dùng - Nguyễn Quang Huy
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
    public static void loadMyScore(MyCompleteListener myCompleteListener)
    {//Hàm này xử lý tải tổng điểm số của người dùng - Nguyễn Quang Huy
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
    public static void cancelBookmarks(MyCompleteListener myCompleteListener)
    {//Hàm này xử lý hủy lưu câu hỏi trong danh sách "Câu hỏi đã lưu" của người dùng - Nguyễn Quang Huy
        WriteBatch batch = g_firestore.batch();
        Map<String,Object> bmData = new ArrayMap<>();
        for(int i = 0 ; i < g_bmIdList.size(); i++){
            bmData.put("BM"+String.valueOf(i+1)+"_ID",g_bmIdList.get(i));
        }
        DocumentReference bmDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("BOOKMARKS");
        batch.set(bmDoc,bmData);

        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid());

        batch.update(userDoc,"BOOKMARKS",g_bmIdList.size());

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        myCompleteListener.onSucces();;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }
    public static void saveResult(int score, MyCompleteListener myCompleteListener)
    {//Hàm này xử lý cập nhập điểm người dùng sau khi hoàn thành bài làm - Nguyễn Quang Huy
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
                                        if (documentSnapshot.exists()) {
                                            Map<String, Object> data = documentSnapshot.getData();
                                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                                Object value = entry.getValue();
                                                if (value instanceof Number) {
                                                    top += ((Number) value).intValue();
                                                }
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

    public static void loadQuestions(MyCompleteListener myCompleteListener)
    {//Hàm này xử lý tải danh sách câu hỏi theo danh mục và tên bài kiểm tra - Bành Viết Hùng
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
    public static void getUserData(MyCompleteListener myCompleteListener)
    {//Hàm này xử lý tải tất cả thông tin người dùng khi đăng nhập vào hệ thống - Bành Viết Hùng
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

    public static void createUserData(String email, String name, MyCompleteListener myCompleteListener)
    {//Hàm này xử lý thêm thông tin người dùng vào database - Bành Viết Hùng
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
    {//Hàm này xử lý tải danh sách danh mục - Bành Viết Hùng
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

    public  static  void loadTestData(MyCompleteListener myCompleteListener)
    {//Hàm này xử lý tải danh sách bài kiểm tra theo danh mục - Bành Viết Hùng
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
    public static void loadData(MyCompleteListener myCompleteListener)
    {//Hàm này xử lý load các thông tin như (Danh mục,thông tin người dùng,số lượng người dùng) - Bành Viết Hùng
        loadCategories(new MyCompleteListener()
        {
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
