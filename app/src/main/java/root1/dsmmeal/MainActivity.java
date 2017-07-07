package root1.dsmmeal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    /**
     * A placeholder fragment containing a simple view.


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Date today = new Date();
        private Context context;


        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(Calendar.DATE, position);
            return new MyFragment(simpleDateFormat.format(calendar.getTime()),context);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 30;
        }
    }
}

class MyFragment extends Fragment{

    public static String TAG = "DSMmeal";

    private View saveView = null;


    private String curruntStrDate;
    private TextView breakfast, lunch, dinner;
    private TextView day, month, year;
    private String breakfastText = "로딩 중 입니다.", lunchText = "로딩 중 입니다.", dinnerText = "로딩 중 입니다.";

    public MyFragment(String date, Context context){

        curruntStrDate = date;

        AQuery aQuery = new AQuery(context);
        aQuery.ajax("http://dsm2015.cafe24.com/meal?date="+date, String.class,new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                Log.d(TAG, "callback: "+url);
                Log.d(TAG, "callback: "+object);

                try{
                    JSONObject jsonObject = new JSONObject(object);
                    breakfastText = strToArray(jsonObject.getString("breakfast"));
                    breakfast.setText(breakfastText);
                    lunchText = strToArray(jsonObject.getString("lunch"));
                    lunch.setText(lunchText);
                    dinnerText = strToArray(jsonObject.getString("dinner"));
                    dinner.setText(dinnerText);
                }catch (Exception e){
                    Log.d(TAG, "callback: "+e.getMessage());
                }

            }
        });
    }

    private void setDate(){

    }

    private String strToArray(String object) {
        try{
            String sendStr = "";
            JSONArray jsonArray = new JSONArray(object);
            for(int i=0;i<jsonArray.length();i++){
                if(jsonArray.getString(i).contains("amp;")){
                    String tempStr = jsonArray.getString(i).replace("amp;","");
                    sendStr += tempStr + " ";
                }else{
                    sendStr += jsonArray.getString(i) + " ";
                }
            }
            return sendStr;
        }catch(Exception e){
            Log.d(TAG, "strToArray: " + e.getMessage());
            return "오류가 발생했습니다";
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(saveView == null){
            View view = inflater.inflate(R.layout.fragment_card, container,false);
            breakfast = (TextView) view.findViewById(R.id.breakfastText);
            lunch = (TextView) view.findViewById(R.id.lunchText);
            dinner = (TextView) view.findViewById(R.id.dinnerText);
            day = (TextView) view.findViewById(R.id.dayText);
            month = (TextView) view.findViewById(R.id.monthText);
            year = (TextView) view.findViewById(R.id.yearText);

            try{
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date curruntDate = simpleDateFormat.parse(curruntStrDate);
                simpleDateFormat.applyPattern("yyyy");
                year.setText(simpleDateFormat.format(curruntDate));
                simpleDateFormat.applyPattern("MMMM");
                month.setText(simpleDateFormat.format(curruntDate));
                simpleDateFormat.applyPattern("dd");
                day.setText(simpleDateFormat.format(curruntDate));
            }catch (Exception e){
                Log.d(TAG, "MyFragment: "+ e.getMessage());
            }
            saveView = view;
            return view;
        }else{
            return saveView;
        }
    }


}
