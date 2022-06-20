package ex.next.watertanker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ex.next.watertanker.common.RecyclerAdapter;
import ex.next.watertanker.common.RecyclerCallback;
import ex.next.watertanker.common.RecyclerClickInterface;
import ex.next.watertanker.databinding.ActivityUserBinding;
import ex.next.watertanker.databinding.RowItemBinding;
import ex.next.watertanker.models.Orders;
import ex.next.watertanker.models.Users;
import ex.next.watertanker.utils.PrefsUtils;
import ex.next.watertanker.utils.Utils;
import im.delight.android.location.SimpleLocation;

public class UserActivity extends AppCompatActivity implements RecyclerClickInterface {

    Users userDetail;
    List<Users> allSuppliers = new ArrayList<>();
    List<Orders> allOrders = new ArrayList<>();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(Constants.DATABASE_REF_URL)
            .child(Constants.Users);
    DatabaseReference ordersReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(Constants.DATABASE_REF_URL)
            .child(Constants.Orders);
    private SimpleLocation location;
    ActivityUserBinding binding;
    PrefsUtils prefsUtils;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user);
        setContentView(binding.getRoot());

        utils = Utils.getInstance(this);
        location = new SimpleLocation(this);
        if (!location.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }
        utils.latitude = location.getLatitude();
        utils.longitude = location.getLongitude();

        prefsUtils = new PrefsUtils(this);
        userDetail = (Users) prefsUtils.getFromPrefs(Constants.USER_DATA);
        Log.d("usersare", userDetail.getType());

        binding.toolbar.clBack.setOnClickListener(view -> {
            utils.showExitDialogue(this);
        });

        binding.toolbar.tvTitle.setText(String.format(getString(R.string.welcome), userDetail.getfName()));

        binding.toolbar.ivLogout.setOnClickListener(view -> {
            utils.showLogoutDialogue(this, prefsUtils, LoginScreen.class);
        });

        binding.btnMyOrders.setOnClickListener(view -> {
            ordersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        allOrders.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if (data.getKey().contains(utils.users.getKey())) {
                                startActivity(new Intent(UserActivity.this, OrdersActivity.class));
                                break;
                            }
                        }
                    } else {
                        Toast.makeText(UserActivity.this, getString(R.string.no_data_exists), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        location.setListener(() -> {
            utils.latitude = location.getLatitude();
            utils.longitude = location.getLongitude();
        });

        setApiKeyForApp();
        setupMap();

    }

    private void setRecyclerData(RowItemBinding binder, Users model) {
        binder.setUser(model);
        binder.setHandler(this);
    }

    @Override
    protected void onPause() {
        location.endUpdates();
        binding.mapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        location.beginUpdates();
        binding.mapView.resume();
        super.onResume();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allSuppliers.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!data.getValue(Users.class).getType().equals(Constants.TYPE_USER)) {
                        allSuppliers.add(data.getValue(Users.class));
                    }
                }
                if (allSuppliers.size() > 0) {
                    addGraphics(binding.mapView, allSuppliers);
                    populateSuppliersRv((ArrayList<Users>) allSuppliers);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled: ", databaseError.toException().toString());
            }
        });
    }

    private void populateSuppliersRv(ArrayList<Users> allSuppliers) {
        RecyclerAdapter adapter = new RecyclerAdapter(this, allSuppliers, R.layout.row_item, new RecyclerCallback<RowItemBinding, Users>() {
            @Override
            public void bindData(RowItemBinding binder, Users model) {
                setRecyclerData(binder, model);
            }
        });

        binding.rvSuppliers.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        binding.mapView.dispose();
        super.onDestroy();
    }

    private void setupMap() {
        // create a map with the BasemapStyle streets
        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

        // set the map to be displayed in the layout's MapView
        binding.mapView.setMap(map);

        // set the viewpoint, Viewpoint(latitude, longitude, scale)
        binding.mapView.setViewpoint(new Viewpoint(utils.latitude, utils.longitude, 200000.0));

//        drawMarker(binding.mapView);
    }

    private void addGraphics(MapView mapView, List<Users> allSuppliers) {
        // create a graphics overlay and add it to the map view
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        /*List<Point> graphicPoints = new ArrayList<>();
        graphicPoints.add(new Point(utils.longitude, utils.latitude, SpatialReferences.getWgs84()));
        graphicPoints.add(new Point(73.09426582428578, 33.48969980302564, SpatialReferences.getWgs84()));*/

        for (int i = 0; i < allSuppliers.size(); i++) {
            SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, -0xa8cd, 30f);
            SimpleLineSymbol blueOutlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 2f);
            simpleMarkerSymbol.setOutline(blueOutlineSymbol);
            // create a graphic with the point geometry and symbol
            Graphic pointGraphic = new Graphic(new Point(Double.parseDouble(allSuppliers.get(i).getLongitude()), Double.parseDouble(allSuppliers.get(i).getLatitude()),
                    SpatialReferences.getWgs84()), simpleMarkerSymbol);
            // add the point graphic to the graphics overlay
            graphicsOverlay.getGraphics().add(pointGraphic);
        }

//        mapView.performContextClick()

    }


    private void setApiKeyForApp() {
        ArcGISRuntimeEnvironment.setApiKey("AAPK4cb1664fb696490a8f735e8f6e6e3454brnPQ6ogn3sOJROVOVmMzEQFVuCawkUwNWIy_z2WeJWMWoYQy_mDd-rMHgIlZZ0-");
    }

    @Override
    public void onClick(Users user) {
        utils.showSupplierDetailsDialogue(this, user, userDetail);
    }

    @Override
    public void onOrderClick(Orders orders) {

    }

    @Override
    public void onAccept(Orders orders) {

    }

    @Override
    public void onReject(Orders orders) {

    }

    @Override
    public void onTimeSelection(Orders orders) {

    }
}