package eu.ase.ro.grupa1086.licentamanolachemariacatalina.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.MainActivity;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodInfo;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.OrderDetails;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.OrdersList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.PrincipalMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderAdminViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderDetailsViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderViewHolder;

public class AdminOrders extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageView startDate;
    ImageView endDate;
    TextView tvStartDate;
    TextView tvEndDate;
    ImageView search;

    Date firstDate;
    Date lastDate;
    Date firstDate2;
    Date lastDate2;

    int result;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    public RecyclerView.LayoutManager secondLayoutManager;
    FirebaseRecyclerAdapter<Order, OrderAdminViewHolder> adapter;
    FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder> secondAdapter;

    DatePickerDialog.OnDateSetListener setListener;
    DatePicker datePicker;
    SimpleDateFormat sdf;
    SimpleDateFormat sdf2;

    FirebaseDatabase database;
    DatabaseReference ordersHistory;
    DatabaseReference restaurantAddresses;
    DatabaseReference cart;
    DatabaseReference users;

    String restaurantName;
    String restaurantImage;
    String orderId;
    List<Order> orderList = new ArrayList<Order>();

    AlertDialog.Builder signOutAlert;
    FloatingActionButton addNewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        recyclerView = (RecyclerView) findViewById(R.id.adminOrdersList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance();
        ordersHistory = FirebaseDatabase.getInstance().getReference("ordersHistory");
        users = database.getReference("users");

        startDate = findViewById(R.id.pickStartDate);
        endDate = findViewById(R.id.pickEndDate);

        tvStartDate = findViewById(R.id.pickedStartDate);
        tvEndDate = findViewById(R.id.pickedEndDate);

        search = findViewById(R.id.btnSearch);
        addNewCategory = findViewById(R.id.fabAddNewCategory);

        addNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewCategory.class));
                overridePendingTransition(R.anim.slide_up, R.anim.slide_nothing);
            }
        });

        signOutAlert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));

        sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT);
        sdf2 = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);


        Date objDate = new Date(); // Current System Date and time is assigned to objDate
        //System.out.println(sdf.format(objDate));

        Calendar cal = Calendar.getInstance();
        cal.setTime(objDate);
        cal.add(Calendar.DATE, -1);
        Date objDateMinusOneDay = cal.getTime();

        try {
            firstDate = sdf.parse(sdf.format(objDateMinusOneDay));
            lastDate = sdf.parse(sdf.format(objDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvStartDate.setText(sdf2.format(objDateMinusOneDay));
        tvEndDate.setText(sdf2.format(objDate));

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AdminOrders.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String shownDate = day + "." + month + "." + year;
                        String date = day + "." + month + "." + year + " 00:00:01";
                        try {
                            firstDate2 = sdf.parse(date);

                            if (lastDate != null) {
                                result = DateDifference.compareDates(firstDate2, lastDate);
                                if (result == 0) {
                                    tvStartDate.setText(shownDate);
                                    //loadOrders();
                                    firstDate = firstDate2;
                                    //loadOrdersWithStartDate(date);
                                } else if (result == 1) {
                                    Toast.makeText(getApplicationContext(), "Data de început nu poate să fie după data de sfârșit", Toast.LENGTH_SHORT).show();

                                } else if (result == -1) {
                                    Toast.makeText(getApplicationContext(), "Data de sfârșit nu poate să fie înainte de data de început", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                tvStartDate.setText(shownDate);
                                firstDate = firstDate2;
                                //loadOrders();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

//        setListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                month = month + 1;
//                String date = day + "/" + month + "/" + year;
//                tvStartDate.setText(date);
//            }
//        };

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AdminOrders.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String shownDate = day + "." + month + "." + year;
                        String date = day + "." + month + "." + year + " 23:59:59";
                        try {
                            lastDate2 = sdf.parse(date);

//                            if (lastDate.after(objDate)) {
//                                Toast.makeText(getApplicationContext(), "Nu se pot seta date din viitor", Toast.LENGTH_SHORT).show();
//                            }
//                            else
                            if (firstDate != null) {
                                result = DateDifference.compareDates(firstDate, lastDate2);
                                if (result == 0) {
                                    tvEndDate.setText(shownDate);
                                    lastDate = lastDate2;
                                } else if (result == 1) {
                                    Toast.makeText(getApplicationContext(), "Data de început nu poate să fie după data de sfârșit", Toast.LENGTH_SHORT).show();

                                } else if (result == -1) {
                                    Toast.makeText(getApplicationContext(), "Data de sfârșit nu poate să fie înainte de data de început", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                tvEndDate.setText(shownDate);
                                lastDate = lastDate2;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        loadOrders();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadOrders();
                adapter.startListening();
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.topOrders);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.restaurantAccounts) {
                    startActivity(new Intent(getApplicationContext(), RestaurantAccountsList.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                    return true;
                } else if(id == R.id.driverAccounts) {
                    startActivity(new Intent(getApplicationContext(), DriverAccountsList.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                    return true;
                } else if(id == R.id.topOrders) {
                    return true;
                }
//                switch (item.getItemId()) {
//                    case R.id.restaurantAccounts:
//                        startActivity(new Intent(getApplicationContext(), RestaurantAccountsList.class));
//                        finish();
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.driverAccounts:
//                        startActivity(new Intent(getApplicationContext(), DriverAccountsList.class));
//                        finish();
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.topOrders:
//                        return true;
//                }
                return false;
            }
        });

    }

    private void loadOrders() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("ordersHistory")
                .orderByChild("currentDateAndTime")
                .limitToLast(50);

        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(query, Order.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Order, OrderAdminViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderAdminViewHolder holder, int position, @NonNull Order model) {

                //Log.i("ceva", model.toString());
                restaurantName = null;
                restaurantImage = null;
                orderList.add(model);
                //Log.i("ceva", orderList.toString());
                orderId = model.getId();

                //Log.i("currentDate", model.getCurrentDateAndTime());

                try {
                    if ((firstDate == null && lastDate == null) || (firstDate != null && lastDate != null &&
                            (firstDate.before(sdf.parse(model.getCurrentDateAndTime())) || firstDate.equals(sdf.parse(model.getCurrentDateAndTime())))
                            && (lastDate.after(sdf.parse(model.getCurrentDateAndTime())) || lastDate.equals(sdf.parse(model.getCurrentDateAndTime()))))) {

//                        holder.upArrow.setVisibility(View.VISIBLE);
//                        holder.downArrow.setVisibility(View.GONE);
//                        holder.secondRecyclerView.setVisibility(View.VISIBLE);
//                        holder.orderDateAndTime.setVisibility(View.VISIBLE);
//                        holder.orderAddress.setVisibility(View.VISIBLE);
//                        holder.orderPriceTotal.setVisibility(View.VISIBLE);
//                        holder.orderStatus.setVisibility(View.VISIBLE);
//                        holder.restaurantImage.setVisibility(View.VISIBLE);
//                        holder.restaurantsName.setVisibility(View.VISIBLE);
                        holder.itemView.setVisibility(View.VISIBLE);
                        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                        ordersHistory.child(model.getId()).child("restaurantAddress").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Log.i("ceva", dataSnapshot.toString());
                                    Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                                    if (restaurantName == null) {
                                        restaurantName = restaurant.getName();
                                        restaurantImage = restaurant.getImage();
                                    } else {
                                        restaurantName += ", " + restaurant.getName();
                                    }
                                }

                                users.child(model.getUserId()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User user = snapshot.getValue(User.class);
                                        holder.clientName.setText("#" + user.getName());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                holder.restaurantsName.setText(restaurantName);
                                holder.orderStatus.setText(getString(R.string.order_status) + " " + String.valueOf(model.getStatus()).substring(0, 1).toUpperCase(Locale.ROOT) + String.valueOf(model.getStatus()).replace("_", " ").substring(1));
                                holder.orderAddress.setText(getString(R.string.address) + " " + model.getAddress().getMapsAddress());
                                holder.orderDateAndTime.setText("Data: " + model.getCurrentDateAndTime());
                                holder.orderPriceTotal.setText(getString(R.string.total) + " " + (double) Math.round(model.getTotal() * 100d) / 100d + " " + getString(R.string.lei));
                                Picasso.with(getBaseContext()).load(restaurantImage).placeholder(R.drawable.loading)
                                        .into(holder.restaurantImage);

                                String restaurantName2 = restaurantName;
                                String restaurantImage2 = restaurantImage;
                                restaurantName = null;
                                restaurantImage = null;

                                final Order local = model;
                                holder.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onClick(View view, int position, boolean isLongClick) {

                                        if (!isLongClick) {
                                            //Toast.makeText(OrdersList.this, model.getCart().toString(), Toast.LENGTH_LONG).show();
                                            //                                    loadOrderDetails(model.getId());

                                            if (holder.downArrow.getVisibility() == View.VISIBLE) {
                                                holder.downArrow.setVisibility(View.GONE);
                                            } else {
                                                holder.downArrow.setVisibility(View.VISIBLE);
                                            }

                                            if (holder.upArrow.getVisibility() == View.VISIBLE) {
                                                holder.upArrow.setVisibility(View.GONE);
                                            } else {
                                                holder.upArrow.setVisibility(View.VISIBLE);
                                            }

                                            cart = database.getReference().child("ordersHistory").child(model.getId()).child("cart");
                                            restaurantAddresses = database.getReference("ordersHistory").child(model.getId()).child("restaurantAddress");

                                            Query query = cart
                                                    .orderByChild("id")
                                                    .limitToLast(50);

                                            FirebaseRecyclerOptions<Food> options =
                                                    new FirebaseRecyclerOptions.Builder<Food>()
                                                            .setQuery(query, Food.class)
                                                            .build();

                                            secondAdapter = new FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder>(options) {
                                                @Override
                                                protected void onBindViewHolder(@NonNull OrderDetailsViewHolder holder2, int position, @NonNull Food model) {

                                                    holder2.foodName.setText(model.getName());

                                                    restaurantAddresses.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                                Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                                                                if (model.getRestaurantId().equals(restaurant.getId())) {
                                                                    holder2.restaurantName.setText(restaurant.getName() + " : ");
                                                                }
                                                            }

                                                            holder2.foodPrice.setText(String.valueOf(model.getPrice()));
                                                            holder2.foodQuantity.setText(String.valueOf(model.getQuantity()));
                                                            holder2.foodTotal.setText((double) Math.round(model.getPrice() * model.getQuantity() * 100d) / 100d + " lei");
                                                            Picasso.with(getBaseContext()).load(model.getImage()).placeholder(R.drawable.loading)
                                                                    .into(holder2.foodImage);

                                                            final Food local2 = model;
                                                            holder2.setItemClickListener(new ItemClickListener() {
                                                                @Override
                                                                public void onClick(View view, int position, boolean isLongClick) {


                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });


                                                }


                                                @NonNull
                                                @Override
                                                public OrderDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                                    View view = LayoutInflater.from(parent.getContext())
                                                            .inflate(R.layout.order_detail_layout, parent, false);
                                                    view.setMinimumWidth(parent.getMeasuredWidth());

                                                    return new OrderDetailsViewHolder(view);
                                                }
                                            };

                                            //                                    secondRecyclerView.setAdapter(secondAdapter);
                                            //                                    secondAdapter.startListening();
                                            if (holder.secondRecyclerView.getVisibility() == View.GONE) {
                                                holder.secondRecyclerView.setVisibility(View.VISIBLE);
                                            } else {
                                                holder.secondRecyclerView.setVisibility(View.GONE);
                                            }
                                            secondLayoutManager = new LinearLayoutManager(getApplicationContext());
                                            //holder.secondRecyclerView.setHasFixedSize(true);
                                            holder.secondRecyclerView.setLayoutManager(secondLayoutManager);
                                            holder.secondRecyclerView.setAdapter(secondAdapter);
                                            secondAdapter.startListening();

                                        }

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {

//                        holder.upArrow.setVisibility(View.GONE);
//                        holder.downArrow.setVisibility(View.GONE);
//                        holder.secondRecyclerView.setVisibility(View.GONE);
//                        holder.orderDateAndTime.setVisibility(View.GONE);
//                        holder.orderAddress.setVisibility(View.GONE);
//                        holder.orderPriceTotal.setVisibility(View.GONE);
//                        holder.orderStatus.setVisibility(View.GONE);
//                        holder.restaurantImage.setVisibility(View.GONE);
//                        holder.restaurantsName.setVisibility(View.GONE);
                        holder.itemView.setVisibility(View.GONE);
                        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));


                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {

                            }
                        });


                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


//                if(orderList.size() == 0) {
//                    Log.i("ordersList", String.valueOf(orderList.size()));
//                    imgNoOrderFound.setVisibility(View.VISIBLE);
//                    tvNoOrderFound.setVisibility(View.VISIBLE);
//
//                    tvNoOrderFound.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent principalMenu = new Intent(OrdersList.this, PrincipalMenu.class);
//                            startActivity(principalMenu);
//                            finish();
//                        }
//                    });
//                } else {
//                    imgNoOrderFound.setVisibility(View.GONE);
//                    tvNoOrderFound.setVisibility(View.GONE);
//                }
            }

            @NonNull
            @Override
            public OrderAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_admin_layout, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new OrderAdminViewHolder(view);
            }
        };


        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        //secondAdapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_out_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            signOutAlert.setTitle("Ieșire din cont")
                    .setMessage("Ești sigur că dorești să ieși din cont?")
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                            overridePendingTransition(R.anim.slide_nothing, R.anim.slide_down);

                        }
                    }).setNegativeButton("Nu", null)
                    .create().show();
        }
        return false;
    }

}