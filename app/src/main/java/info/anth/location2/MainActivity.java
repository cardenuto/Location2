package info.anth.location2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Stones");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add a new Stone", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // Need to set the default of the current step for adding a new stone
                Intent intent = new Intent(view.getContext(), StoneActivity.class);
                intent.putExtra(StoneActivity.REQUEST_CURRENT_STEP, "gps");
                view.getContext().startActivity(intent);
            }
        });

        // test image
        //ImageView testImage = (ImageView) findViewById(R.id.test_image);
        //testImage.setImageResource(R.drawable.logo);

        //Cloudinary cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this));
        //Cloudinary cloudinary = LocationApplication.getInstance(this).getCloudinary();
        //String url_string = cloudinary.url().transformation(new Transformation().width(100).height(150).crop("fill")).generate("sample.jpg");
        //Log.i("ajc", cloudinary.url().transformation(new Transformation().width(100).height(150).crop("fill")).generate("sample.jpg"));
        //ImageLoader.getInstance().displayImage(url_string, testImage);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
