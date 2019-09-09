package androidx.lifecycle

/**
 * ViewModel is a class that is responsible for preparing and managing the data for an `Activity`
 * or a `Fragment`. It also handles the communication of the Activity / Fragment with the rest of
 * the application (e.g. calling the business logic classes).
 *
 * A ViewModel is always created in association with a scope (a fragment or an activity) and will
 * be retained as long as the scope is alive. E.g. if it is an Activity, until it is
 * finished.
 *
 * In other words, this means that a ViewModel will not be destroyed if its owner is destroyed for a
 * configuration change (e.g. rotation). The new instance of the owner will just re-connected to the
 * existing ViewModel.
 *
 * The purpose of the ViewModel is to acquire and keep the information that is necessary for an
 * Activity or a Fragment. The Activity or the Fragment should be able to observe changes in the
 * ViewModel. ViewModels usually expose this information via LiveData or Android Data
 * Binding. You can also use any observability construct from you favorite framework.
 *
 * ViewModel's only responsibility is to manage the data for the UI. It **should never** access
 * your view hierarchy or hold a reference back to the Activity or the Fragment.
 *
 * Typical usage from an Activity standpoint would be:
 *
 * ```
 * public class UserActivity extends Activity {
 *
 *     @Override
 *     protected void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         setContentView(R.layout.user_activity_layout);
 *         final UserModel viewModel = ViewModelProviders.of(this).get(UserModel.class);
 *         viewModel.userLiveData.observer(this, new Observer<User>() {
 *             @Override
 *             public void onChanged(@Nullable User data) {
 *                 // update ui.
 *             }
 *         });
 *         findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
 *             @Override
 *             public void onClick(View v) {
 *                  viewModel.doAction();
 *             }
 *         });
 *     }
 * }
 * ```
 *
 * ViewModel would be:
 *
 * ```
 * public class UserModel extends ViewModel {
 *     public final LiveData<User> userLiveData = new LiveData<>();
 *
 *     public UserModel() {
 *         // trigger user load.
 *     }
 *
 *     void doAction() {
 *         // depending on the action, do necessary business logic calls and update the
 *         // userLiveData.
 *     }
 * }
 * ```
 *
 *
 * ViewModels can also be used as a communication layer between different Fragments of an Activity.
 * Each Fragment can acquire the ViewModel using the same key via their Activity. This allows
 * communication between Fragments in a de-coupled fashion such that they never need to talk to
 * the other Fragment directly.
 *
 * ```
 * public class MyFragment extends Fragment {
 *     public void onStart() {
 *         UserModel userModel = ViewModelProviders.of(getActivity()).get(UserModel.class);
 *     }
 * }
 * ```
 *
 */
abstract class ViewModel {
    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     *
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel.
     */
    protected open fun onCleared() {
    }
}
