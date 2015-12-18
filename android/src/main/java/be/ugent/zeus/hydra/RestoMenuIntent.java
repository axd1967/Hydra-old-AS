package be.ugent.zeus.hydra;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;

import java.util.List;

/** Interface for external apps to invoke the Hydra menu activity
 *
 * <br>This class provides {@link RestoMenuIntent#invokeHydraResto} that other apps should use to invoke the Hydra menu activity.
 * <br>If Hydra is not installed, a dialog will allow the user to visit the Play store to install Hydra.
 * <br>Note that external apps could do without this file, but it will be more difficult to manage updates (and build the intent, etc); if you follow the vendor branch approach, life will be easier.
 *
 * <h2>Installing</h2>
 * To install files from external sources (Hydra source code) and be able to tweak those locally, follow the <b>vendor branch</b> approach.
 * <ol>
 *     <li>Checkout a "vendor" branch <b>vendor/hydra</b></li>
 *     <li>Copy this file under <code>be.ugent.hydra</code></li>
 *     <li>Commit; remember to add the Hydra version in the commit message</li>
 *     <li>Apply a vendor tag <b>vendor/hydra/x.y.z</b></li>
 * </ol>
 * And keep an eye on <a href="https://github.com/ZeusWPI/hydra">changes</a>.
 * <h2>Updates</h2>
 *
 * <ol>
 *     <li>Checkout your Hydra vendor branch</li>
 *     <li>Replace this file with its new version</li>
 *     <li>Commit</li>
 *     <li>Apply a fresh vendor tag</li>
  * </ol>
 *
 * After these operations, switch to your working branch and merge the (updated) vendor branch. If you like, you can now further customise your <i>local copy</i> of this package (and benefit later from vendor updates).
 *
 * @author alex on 18/12/2015.
 */
public class RestoMenuIntent {

    private static final String LOGTAG = RestoMenuIntent.class.getSimpleName();

    private static final String PACKAGE = "be.ugent.zeus.hydra";
    private static final String RESTO_ACTIVITY = PACKAGE + ".RestoMenu";

//    public static final String EXTRA_RESTO_INT = "resto_id";

    /** invoke Hydra resto menu via Intent
     *
     */
    public static void invokeHydraResto( Context ctx  /*, int restoID  */) {

        // Build the intent
        Intent hydraIntent = new Intent();

        hydraIntent
                .setComponent(new ComponentName(PACKAGE, RESTO_ACTIVITY))
        .setAction(Intent.ACTION_VIEW)
//                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                .putExtra(EXTRA_RESTO_INT, restoID)
        ;

        // Verify it resolves
        PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(hydraIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Start activity if it's safe
        if (!isIntentSafe) {
            showDownloadDialog(ctx);
            return;
        }

        ctx.startActivity(hydraIntent);

    }

    private static void showDownloadDialog(final Context from) {

        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(from);
        downloadDialog.setTitle("Hydra not found");
        downloadDialog.setMessage("Launch Google Play to install Hydra?");
        downloadDialog.setPositiveButton("yes",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=" + PACKAGE));
                        try
                        {
                            from.startActivity(intent);
                        }
                        catch (ActivityNotFoundException e)
                        {
                            Toast.makeText(from, "ERROR: URL not found!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        downloadDialog.setNegativeButton("no",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int i)
                    {
                        dialog.dismiss();
                    }
                });
        downloadDialog.show();
    }

}
