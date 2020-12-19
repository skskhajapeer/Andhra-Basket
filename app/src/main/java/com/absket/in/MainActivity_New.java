package com.absket.in;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.absket.in.CustomFonts.MyTextView;
import com.absket.in.CustomFonts.MyTextViewBold;
import com.absket.in.adapter.CustomAdapter;
import com.absket.in.adapter.ExpandableListAdapter;
import com.absket.in.adapter.MyAdapter;
import com.absket.in.model.SelectedProductBean;
import com.absket.in.model.Singleton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity_New extends AppCompatActivity {

    RecyclerView grid_main_category;
    ProgressDialog progressDialog;
    HashMap<String, List<String>> listDataChild;
    List<String> listDataHeader;
    UserSessionManager session;
    private TextView badgeText = null;
    ListView list_ss;
    ViewPager pager;
    CircleIndicator indicator;
    ArrayAdapter adapterSS;
    ViewPagerAdapterImages pageradapterImages;
    LinearLayout linear_myaccount;
    Handler handler;
    int iCount = 0;

    ExpandableListAdapter listAdapter;

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    Toolbar toolbar;

    //DrawerMenu Initializations
    CardView card_drawer_login;
    MyTextView tv_myprofile;
    LinearLayout customer_linear;
    LinearLayout notifications_linear;
    LinearLayout linear_myaccount_expand;
    ImageView img_myaccount_plus;
    ImageView img_sbo_plus;
    LinearLayout linear_sbo_expand;
    ImageView img_ss_plus;
    LinearLayout linear_ss_expand;
    ImageView img_sbc_plus;
    LinearLayout linear_shopbycategory;
    ExpandableListView lvExp;
    MyTextViewBold tv_mail;
    MyTextView tv_logout;
    TextView tv_refer;
    MyTextView tv_myorders;
    LinearLayout home_linear;
    LinearLayout last_purchase_layout;
    LinearLayout linear_expandable;
    LinearLayout linear_main_drawer;
    LinearLayout linear_specialshops;
    TextView tv_name;
    LinearLayout faqs_layout;
    TextView tv_aboutus;
    CardView card_main_search;
    ImageView img_back_todrawer;
    FloatingActionButton fab_cart;


    int lastExpandedPosition = -1;
    MyTextViewBold tv_name_facebook;
    MyTextViewBold tv_mail_facebook;
    public static final String KEY_EXTRA = "facebook";
    String facebook = "facebook";
    MyTextView tv_changepassword;
    String normallogin = "normallogin";
    View view_lastpurchase;
    LinearLayout logout_layout;
    LinearLayout refer_layout;

MyAdapter myAdapter;
    public static String[] osNameList = {
            "Fruits & Vegetables",
            "Grocery & Staples ",
            "Best Sellers",
            "Household",
            "DRY FRUITS",
            "Drinks",
            "Snacks Products",
            "Personal Care",
            "Packaged Foods",
            "Meat & Eggs",
            "Drinking Water",
            "Baby Care",
    };
    public static int[] osImages = {
            R.mipmap.fruitsveg,
            R.mipmap.grocery,
            R.mipmap.bestseller,
            R.mipmap.household,
            R.mipmap.dryfruits,
            R.mipmap.drinks,
            R.mipmap.snacks,
            R.mipmap.personal,
            R.mipmap.packagedfood,
            R.mipmap.meateggs,
            R.mipmap.drinkingwater,
            R.mipmap.babycare,};



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        badgeText = (TextView) findViewById(R.id.badge_text);
        badgeText.setText("");
        view_lastpurchase = (View) findViewById(R.id.view_lastpurchase);
        //  linear_myaccount = (LinearLayout) findViewById(R.id.linear_myaccount);
        grid_main_category = (RecyclerView) findViewById(R.id.grid_main_category);
        refer_layout = (LinearLayout) findViewById(R.id.refer_layout);
        logout_layout = (LinearLayout) findViewById(R.id.logout_layout);
        pager = (ViewPager) findViewById(R.id.pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nvView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //DrawerMenu Initializations
        card_drawer_login = (CardView) findViewById(R.id.card_drawer_login);
        tv_myprofile = (MyTextView) findViewById(R.id.tv_myprofile);
        customer_linear = (LinearLayout) findViewById(R.id.customer_linear);
        notifications_linear = (LinearLayout) findViewById(R.id.notifications_linear);
        tv_changepassword = (MyTextView) findViewById(R.id.tv_changepassword);
        linear_myaccount_expand = (LinearLayout) findViewById(R.id.linear_myaccount_expand);
        img_myaccount_plus = (ImageView) findViewById(R.id.img_myaccount_plus);
        img_sbo_plus = (ImageView) findViewById(R.id.img_sbo_plus);
        linear_sbo_expand = (LinearLayout) findViewById(R.id.linear_sbo_expand);
        img_ss_plus = (ImageView) findViewById(R.id.img_ss_plus);
        linear_ss_expand = (LinearLayout) findViewById(R.id.linear_ss_expand);
        img_sbc_plus = (ImageView) findViewById(R.id.img_sbc_plus);
        linear_shopbycategory = (LinearLayout) findViewById(R.id.linear_shopbycategory);
        lvExp = (ExpandableListView) findViewById(R.id.lvExp);
        tv_mail = (MyTextViewBold) findViewById(R.id.tv_mail);
        tv_logout = (MyTextView) findViewById(R.id.tv_logout);
        tv_refer = (TextView) findViewById(R.id.tv_refer);
        tv_myorders = (MyTextView) findViewById(R.id.tv_myorders);
        home_linear = (LinearLayout) findViewById(R.id.home_linear);
        last_purchase_layout = (LinearLayout) findViewById(R.id.last_purchase_layout);
        tv_name = (TextView) findViewById(R.id.tv_name);


        tv_name_facebook = (MyTextViewBold) findViewById(R.id.tv_name_facebook);
        tv_mail_facebook = (MyTextViewBold) findViewById(R.id.tv_mail_facebook);
        faqs_layout = (LinearLayout) findViewById(R.id.faqs_layout);
        tv_aboutus = (TextView) findViewById(R.id.tv_aboutus);
        badgeText = (TextView) findViewById(R.id.badge_text);
        badgeText.setText("");

        linear_myaccount = (LinearLayout) findViewById(R.id.linear_myaccount);
        linear_expandable = (LinearLayout) findViewById(R.id.linear_expandable);
        linear_main_drawer = (LinearLayout) findViewById(R.id.linear_main_drawer);
        linear_specialshops = (LinearLayout) findViewById(R.id.linear_specialshops);


//

        card_main_search = (CardView) findViewById(R.id.card_main_search);
        img_back_todrawer = (ImageView) findViewById(R.id.img_back_todrawer);
        list_ss = (ListView) findViewById(R.id.list_ss);
        pager = (ViewPager) findViewById(R.id.pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        fab_cart = (FloatingActionButton) findViewById(R.id.fab_cart);


        session = new UserSessionManager(MainActivity_New.this);


        if (getIntent().hasExtra(KEY_EXTRA)) {
            facebook = getIntent().getStringExtra(KEY_EXTRA);
            normallogin = getIntent().getStringExtra(KEY_EXTRA);
        }


        if (session.getIsLoggedIn().equals("true")) {
            tv_mail.setVisibility(View.VISIBLE);
            tv_mail.setText(session.getUserEmail());
            card_drawer_login.setVisibility(View.GONE);
            tv_name.setText(session.getUserName());
            tv_name.setVisibility(View.VISIBLE);
            //  tv_refer.setVisibility(View.VISIBLE);
            refer_layout.setVisibility(View.VISIBLE);
            logout_layout.setVisibility(View.VISIBLE);
            tv_myorders.setVisibility(View.VISIBLE);
            last_purchase_layout.setVisibility(View.VISIBLE);
            view_lastpurchase.setVisibility(View.VISIBLE);

        } else {
            card_drawer_login.setVisibility(View.VISIBLE);
            tv_name.setVisibility(View.GONE);
            //tv_refer.setVisibility(View.GONE);
            logout_layout.setVisibility(View.GONE);
            refer_layout.setVisibility(View.GONE);

            tv_myorders.setVisibility(View.GONE);
            last_purchase_layout.setVisibility(View.GONE);
            view_lastpurchase.setVisibility(View.GONE);


        }

        fab_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity_New.this, ReviewOrdersActivity.class);
                startActivity(in);
            }
        });
        tv_myorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity_New.this, MyOrdersActivity.class);
                startActivity(in);
            }
        });
        tv_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity_New.this, ChangePassword.class);
                startActivity(in);
                // mDrawerLayout.closeDrawers();

            }
        });
        refer_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity_New.this, ReferActivity.class);
                startActivity(in);
            }
        });

        home_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity_New.this, "Welcome to Andhra Basket", Toast.LENGTH_SHORT).show();
                mDrawerLayout.closeDrawers();
            }
        });

        last_purchase_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity_New.this, LastPurchaseActivity.class);
                startActivity(in);
            }
        });

        faqs_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity_New.this, FAQActivity.class);
                startActivity(in);
            }
        });

        tv_aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity_New.this, AboutUsActivity.class);
                startActivity(in);
            }
        });

        notifications_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity_New.this, NotificationActivity.class);
                startActivity(in);
            }
        });

        logout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Singleton.getInstance().selectedItemsList.clear();
                new android.support.v7.app.AlertDialog.Builder(MainActivity_New.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                session.setIsLoggedIn("");

                                Intent in = new Intent(MainActivity_New.this, LoginActivity.class);
                                finish();
                                startActivity(in);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(R.drawable.logout)
                        .show();
            }
        });


        card_main_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity_New.this, SearchActivity.class);
                startActivity(in);
            }
        });


        linear_myaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linear_myaccount_expand.isShown()) {
                    linear_myaccount_expand.setVisibility(View.GONE);
                    img_myaccount_plus.setRotation(-90);
                    img_myaccount_plus.setBackgroundResource(R.drawable.drop_down);
                } else {
                    linear_myaccount_expand.setVisibility(View.VISIBLE);
                    img_myaccount_plus.setRotation(180);
                    img_myaccount_plus.setBackgroundResource(R.drawable.drop_down);
                }
            }
        });


        img_sbo_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linear_sbo_expand.isShown()) {
                    linear_sbo_expand.setVisibility(View.GONE);
                } else {
                    linear_sbo_expand.setVisibility(View.VISIBLE);
                }
            }
        });


        linear_shopbycategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lvExp.isShown()) {
                    lvExp.setVisibility(View.GONE);
                } else {
                    lvExp.setVisibility(View.VISIBLE);
                    linear_expandable.setVisibility(View.VISIBLE);
                    linear_main_drawer.setVisibility(View.GONE);
                }
            }
        });


        img_back_todrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvExp.setVisibility(View.GONE);
                linear_expandable.setVisibility(View.GONE);
                linear_main_drawer.setVisibility(View.VISIBLE);
            }
        });

        linear_specialshops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity_New.this, SpecialShopsActivity.class);
                in.putExtra("maincategory", "Special Shops");
                startActivity(in);
            }
        });


        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();


        card_drawer_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity_New.this, LoginActivity.class);
                startActivity(in);
            }
        });


        customer_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity_New.this, CustomerServiceActivity.class);
                startActivity(in);
            }
        });

        tv_myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.getIsLoggedIn().equals("true")) {
                    Intent in = new Intent(MainActivity_New.this, MyProfileActivity.class);
                    startActivity(in);
                } else {
                    Toast.makeText(MainActivity_New.this, "Please Login to see your profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
       /* tv_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                Intent in = new Intent(MainActivity.this, ChangePassword.class);
                startActivity(in);

            }
        });*/
      /*  tv_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity_New.this, NotificationActivity.class);
                startActivity(in);
            }
        });*/


        CallingMainCategoryRetrofit();

        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                iCount = iCount + 1;
                if (iCount >= 4) {
                    iCount = 0;
                    pager.setCurrentItem(iCount);
                } else pager.setCurrentItem(iCount);
                handler.postDelayed(this, 4000);
            }
        };

        handler.postDelayed(r, 1000);
       /* LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.headerview, grid_main_category, false);
        grid_main_category.addHeaderView(header, null, false);
        pager = (ViewPager) header.findViewById(R.id.pager);
        indicator = (CircleIndicator) header.findViewById(R.id.indicator);*/
    }


    public void CallingMainCategoryRetrofit() {
        progressDialog = new ProgressDialog(MainActivity_New.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        VariableHolder.arrayCategory = new ArrayList<>();
        VariableHolder.arrayImages = new ArrayList<>();
        VariableHolder.arraySS = new ArrayList<>();
        VariableHolder.arraySSimage = new ArrayList<>();
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "getmaincategory.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.GetMainCategory(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    progressDialog.dismiss();
                    JSONArray jsonArray = new JSONArray(output);
                    int iEcount = -1;
                    List<String> header = null;


                    for (int i = 0; i < jsonArray.length(); i++) {
                        Log.e("RAM", "=============");
                        Log.e("RAM", jsonArray.getJSONObject(i).getString("main_category"));
                        if (jsonArray.getJSONObject(i).getString("main_category").contains("Special")) {
                            VariableHolder.arraySS.add(jsonArray.getJSONObject(i).getString("subcat_name"));
                            VariableHolder.arraySSimage.add(jsonArray.getJSONObject(i).getString("subcat_image"));
                        } else {


                            Log.e("RAM", jsonArray.getJSONObject(i).getString("subcat_name"));


                            String category = jsonArray.getJSONObject(i).getString("main_category");
                            String item = jsonArray.getJSONObject(i).getString("subcat_name");

                            List<String> header1 = new ArrayList<>();
                            for (Map.Entry<String, List<String>> entry : listDataChild.entrySet()) {
                                if (entry.getKey().equalsIgnoreCase(category)) {
                                    header1 = entry.getValue();
                                    break;
                                }
                            }

                            if (header1.size() == 0) {
                                VariableHolder.arrayCategory.add(jsonArray.getJSONObject(i).getString("main_category"));
                                VariableHolder.arrayImages.add(jsonArray.getJSONObject(i).getString("maincat_image"));
                                header = new ArrayList<String>();
                                header.add(item);
                                listDataChild.put(category, header);
                                listDataHeader.add(category);
                            } else {
                                header1.add(jsonArray.getJSONObject(i).getString("subcat_name"));
                                listDataChild.put(category, header1);
                            }
                        }
                    }

                    myAdapter = new MyAdapter(MainActivity_New.this, osNameList,osImages);

                    GridLayoutManager layoutManager
                            = new GridLayoutManager(MainActivity_New.this, 3);
                    grid_main_category.setHasFixedSize(true);
                    grid_main_category.setNestedScrollingEnabled(false);
                    grid_main_category.setLayoutManager(layoutManager);
                    grid_main_category.setAdapter(myAdapter);
                   // grid_main_category.setAdapter(new CustomAdapter(MainActivity_New.this, osNameList, osImages));

                    listAdapter = new ExpandableListAdapter(MainActivity_New.this, listDataHeader, listDataChild);
                    lvExp.setAdapter(listAdapter);


                    lvExp.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                        @Override
                        public void onGroupExpand(int groupPosition) {
                            if (lastExpandedPosition != -1
                                    && groupPosition != lastExpandedPosition) {
                                lvExp.collapseGroup(lastExpandedPosition);
                            }
                            lastExpandedPosition = groupPosition;
                        }
                    });

                    if (VariableHolder.arraySS.size() > 0) {
                        adapterSS = new ArrayAdapter(MainActivity_New.this, android.R.layout.simple_spinner_dropdown_item, VariableHolder.arraySS);
                        list_ss.setAdapter(adapterSS);

                        list_ss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent in = new Intent(MainActivity_New.this, SpecialShopsActivity.class);
                                in.putExtra("maincategory", "SpecialShops");
                                startActivity(in);
                            }
                        });
                    }


                   /* lvExp.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                            Toast.makeText(MainActivity.this, "Child Position "+childPosition, Toast.LENGTH_SHORT).show();
                           // listDataChild.ge
                            return false;
                        }
                    });*/

                    CallingSliderImageRetrofit();

                } catch (IOException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(MainActivity_New.this, "Connection Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void CallingSliderImageRetrofit() {
        VariableHolder.arraySliderImages = new ArrayList<>();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "getsliderimages.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.GetSliderImages(session.getCustomerId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    JSONArray jsonArray = new JSONArray(output);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        VariableHolder.arraySliderImages.add(jsonArray.getJSONObject(i).getString("imagename"));
                    }

                    pageradapterImages = new ViewPagerAdapterImages(MainActivity_New.this, VariableHolder.arraySliderImages.size());
                    pager.setAdapter(pageradapterImages);
                    indicator.setViewPager(pager);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MainActivity_New.this, "Connection Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        VariableHolder.sViewPagerImages = "MAIN";

        session = new UserSessionManager(MainActivity_New.this);
        if (session.getIsLoggedIn().equals("true")) {
            linear_myaccount.setVisibility(View.VISIBLE);
        } else {
            linear_myaccount.setVisibility(View.GONE);
        }

        updateCartValues();
    }

    public void updateCartValues() {
        badgeText.setText("");
        int count = 0;
        for (SelectedProductBean bean : Singleton.getInstance().selectedItemsList) {
            count += bean.getQuantity();
        }
        if (count > 0) {
            badgeText.setVisibility(View.VISIBLE);
            badgeText.setText("" + count);
        } else {
            badgeText.setVisibility(View.GONE);
        }

    }


}
