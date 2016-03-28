package hungry.redball.team;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.astuetz.PagerSlidingTabStrip;

import hungry.redball.R;

public class TeamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_p_and_t);

            //for the pre 4.0 device.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Initialize the ViewPager and set an adapter
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(adapter);

            // Bind the tabs to the ViewPager
            PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            tabs.setViewPager(pager);
        }

        public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = { "프리미어","라리가","분데스","세리에","리그1" };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return Frag_team.newInstance(position);
        }
    }
}
