package attend1.com.example.sujith.attend1;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Sujith on 25-08-2015.
 */
public class ParseApplication extends Application {

    @Override
            public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "PuSfDqDFKSKL2yBw41y06EonTGVW7ttKAsUcsZrV", "X7ENZ6p5QNefRyBhWYHTpH4al3kGFrLXE5yqs4gJ");

    }
}
