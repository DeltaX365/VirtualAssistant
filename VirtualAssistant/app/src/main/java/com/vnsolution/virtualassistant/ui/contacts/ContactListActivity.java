package com.vnsolution.virtualassistant.ui.contacts;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.DeviceSetting;
import com.vnsolution.virtualassistant.common.StopTrackingSetting;
import com.vnsolution.virtualassistant.common.UiSetting;
import com.vnsolution.virtualassistant.model.PhoneContact;
import com.vnsolution.virtualassistant.ui.common.BaseActivity;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewAdapter;
import com.vnsolution.virtualassistant.ui.common.RecyclerViewRowTouchListener;
import com.vnsolution.virtualassistant.ui.contacts.component.ContactListSortBottomSheetDialogFragment;
import com.vnsolution.virtualassistant.ui.reminders.ReminderNewActivity;
import com.vnsolution.virtualassistant.util.PermissionUtils;
import com.vnsolution.virtualassistant.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;

public class ContactListActivity extends BaseActivity implements HasAndroidInjector, SearchView.OnQueryTextListener {
    private RecyclerView favouriteContactsRecyclerView;
    private RecyclerView.LayoutManager favouriteContactsLayoutManager;
    private RecyclerViewAdapter<PhoneContact> favouriteContactsAdapter;

    private RecyclerView phoneContactsRecyclerView;
    private RecyclerView.LayoutManager phoneContactsLayoutManager;
    private RecyclerViewAdapter<PhoneContact> phoneContactsAdapter;

    private List<PhoneContact> phoneContacts = new ArrayList<>();
    private ContactListSortBottomSheetDialogFragment.SortBy sortBy = ContactListSortBottomSheetDialogFragment.SortBy.TOTAL_CALL;
    private ContactListSortBottomSheetDialogFragment.SortDirection sortDirection = ContactListSortBottomSheetDialogFragment.SortDirection.DESC;

    @Inject
    public ContactViewModel contactViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        refreshView();
    }

    private void setupView() {
        setContentView(R.layout.screen_contact_list);
        setupActionBar();

        setupFavouriteContactsView();
        setupPhoneContactsView();
    }

    private void refreshView() {
        try {
            startProcessing();
            phoneContacts = contactViewModel.getPhoneContacts(this, false, true);
            refreshFavouriteContactsView();
            refreshPhoneContactsView();
        } finally {
            stopProcessing();
        }
    }

    private void setupFavouriteContactsView() {
        favouriteContactsRecyclerView = findViewById(R.id.list_favourite_contact);
        favouriteContactsRecyclerView.setHasFixedSize(true);

        favouriteContactsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        favouriteContactsRecyclerView.setLayoutManager(favouriteContactsLayoutManager);

        favouriteContactsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiSetting.CONTACT_LIST_ALL, R.layout.card_contact);
        favouriteContactsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneContact>() {
            @Override
            public void onTap(PhoneContact phoneContact) {
                Intent intent = new Intent(ContactListActivity.this, ContactViewActivity.class);
                intent.putExtra("PhoneContactId", phoneContact.id);
                startActivity(intent);
            }
            @Override
            public void onLongTap(PhoneContact phoneContact) {
                makePhoneCall(phoneContact.phoneNumbers);
            }
        });
    }

    private void refreshFavouriteContactsView() {
        List<PhoneContact> favouriteContacts = filterContacts(phoneContacts, 1);
        favouriteContactsAdapter.setData(favouriteContacts);
        favouriteContactsRecyclerView.setAdapter(favouriteContactsAdapter);
    }

    private void setupPhoneContactsView() {
        phoneContactsRecyclerView = findViewById(R.id.list_normal_contact);
        phoneContactsRecyclerView.setHasFixedSize(true);

        phoneContactsLayoutManager = new LinearLayoutManager(this);
        phoneContactsRecyclerView.setLayoutManager(phoneContactsLayoutManager);

        phoneContactsAdapter = new RecyclerViewAdapter<PhoneContact>(this, UiSetting.CONTACT_LIST_ALL, R.layout.card_contact_full_width);
        phoneContactsAdapter.setOnItemClickListener(new RecyclerViewRowTouchListener<PhoneContact>() {
            @Override
            public void onTap(PhoneContact phoneContact) {
                Intent intent = new Intent(ContactListActivity.this, ContactViewActivity.class);
                intent.putExtra("PhoneContactId", phoneContact.id);
                startActivity(intent);
            }

            @Override
            public void onAction(PhoneContact phoneContact, String action) {
                switch (action) {
                    case UiSetting.CONTACT_ACTION_MAKE_FAVOURITE:
                        updatePhoneContact(phoneContact,1);
                        break;
                    case UiSetting.CONTACT_ACTION_REMOVE_FAVOURITE:
                        updatePhoneContact(phoneContact,0);
                        break;
                    case UiSetting.CONTACT_ACTION_CALL:
                        makePhoneCall(phoneContact.phoneNumbers);
                        break;
                    case UiSetting.CONTACT_ACTION_SMS:
                        sendSms(phoneContact.phoneNumbers);
                        break;
                    case UiSetting.CONTACT_ACTION_SCHEDULE:
                        Intent intent = new Intent(ContactListActivity.this, ReminderNewActivity.class);
                        startActivity(intent);
                        break;
                    case UiSetting.CONTACT_ACTION_EXPORT:
                        exportContact(phoneContact);
                        break;
                    case UiSetting.CONTACT_ACTION_SHARE:
                        shareContact(phoneContact);
                        break;
                    case UiSetting.CONTACT_ACTION_REMOVE:
                        removeContact(phoneContact);
                        break;
                }
            }
        });
    }

    private void refreshPhoneContactsView() {
        phoneContactsAdapter.setData(phoneContacts);
        phoneContactsRecyclerView.setAdapter(phoneContactsAdapter);
        sort(sortBy, sortDirection);
    }

    private List<PhoneContact> filterContacts(List<PhoneContact> phoneContacts, int starred) {
        List<PhoneContact> result = new ArrayList<>();
        if (phoneContacts != null) {
            for(PhoneContact phoneContact : phoneContacts) {
                if (phoneContact.starred == starred) {
                    result.add(phoneContact);
                }
            }
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact_list_options, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();
        searchView.setQueryHint("Search your contact");
        searchView.setOnQueryTextListener(this);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setQueryRefinementEnabled(true);
//        searchView.setSubmitButtonEnabled(true);
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_sort:
                ContactListSortBottomSheetDialogFragment bottomSheetDialog = new ContactListSortBottomSheetDialogFragment();
                bottomSheetDialog.listener(new ContactListSortBottomSheetDialogFragment.Listener() {
                    @Override
                    public void onChanged(ContactListSortBottomSheetDialogFragment.SortBy sortByOption, ContactListSortBottomSheetDialogFragment.SortDirection sortDirectionOption) {
                        if (!sortBy.equals(sortByOption) || !sortDirection.equals(sortDirectionOption)) {
                            sortBy = sortByOption;
                            sortDirection = sortDirectionOption;
                            sort(sortBy, sortDirection);
                        }
                    }
                });
                bottomSheetDialog.setValue(sortBy, sortDirection);
                bottomSheetDialog.show(getSupportFragmentManager(), "ContactListSortBottomSheetDialogFragment");
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void sort(ContactListSortBottomSheetDialogFragment.SortBy sortBy, ContactListSortBottomSheetDialogFragment.SortDirection sortDirection) {
        if (sortBy == ContactListSortBottomSheetDialogFragment.SortBy.NAME && sortDirection == ContactListSortBottomSheetDialogFragment.SortDirection.ASC) {
            Collections.sort(phoneContacts, new Comparator<PhoneContact>() {
                @Override
                public int compare(PhoneContact o1, PhoneContact o2) {
                    return o1.name.compareTo(o2.name);
                }
            });
        }
        if (sortBy == ContactListSortBottomSheetDialogFragment.SortBy.NAME && sortDirection == ContactListSortBottomSheetDialogFragment.SortDirection.DESC) {
            Collections.sort(phoneContacts, new Comparator<PhoneContact>() {
                @Override
                public int compare(PhoneContact o1, PhoneContact o2) {
                    return o2.name.compareTo(o1.name);
                }
            });
        }
        if (sortBy == ContactListSortBottomSheetDialogFragment.SortBy.TOTAL_CALL && sortDirection == ContactListSortBottomSheetDialogFragment.SortDirection.ASC) {
            Collections.sort(phoneContacts, new Comparator<PhoneContact>() {
                @Override
                public int compare(PhoneContact o1, PhoneContact o2) {
                    return (o1.totalIncomingNumber + o1.totalOutgoingNumber + o1.totalMissedNumber) -
                            (o2.totalIncomingNumber + o2.totalOutgoingNumber + o2.totalMissedNumber);
                }
            });
        }
        if (sortBy == ContactListSortBottomSheetDialogFragment.SortBy.TOTAL_CALL && sortDirection == ContactListSortBottomSheetDialogFragment.SortDirection.DESC) {
            Collections.sort(phoneContacts, new Comparator<PhoneContact>() {
                @Override
                public int compare(PhoneContact o1, PhoneContact o2) {
                    return (o2.totalIncomingNumber + o2.totalOutgoingNumber + o2.totalMissedNumber) -
                            (o1.totalIncomingNumber + o1.totalOutgoingNumber + o1.totalMissedNumber);
                }
            });
        }
        phoneContactsAdapter.setData(phoneContacts);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, "Query Inserted", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter(newText);
        return true;
    }

    private void filter(String queryText) {
        List<PhoneContact> filteredContacts = new ArrayList<>();
        if (queryText.isEmpty()) {
            filteredContacts.addAll(phoneContacts);
        } else {
            for (PhoneContact contact : phoneContacts) {
                String normalizedName = StringUtils.nonAccentVietnamese(contact.name);
                if (normalizedName.toLowerCase().contains(queryText.toLowerCase())) {
                    filteredContacts.add(contact);
                }
            }
        }
        phoneContactsAdapter.setData(filteredContacts);
    }

    private void updatePhoneContact(PhoneContact phoneContact, int starred) {
        if (phoneContact != null) {
            boolean result = contactViewModel.updatePhoneContact(this, phoneContact.contactId, starred);
            if (result) {
                phoneContact.starred = starred;
                refreshFavouriteContactsView();
                refreshPhoneContactsView();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void makePhoneCall(String phoneNumber) {
        if (requestPermissions() && phoneNumber != null) {
            String[] splitedNumbers = phoneNumber.split(",");
            if (splitedNumbers != null && splitedNumbers.length > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose phone number")
                        .setItems(splitedNumbers, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + splitedNumbers[which]));
                                startActivity(intent);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        }
    }

    private void sendSms(String phoneNumber) {
        if (phoneNumber != null) {
            String[] splitedNumbers = phoneNumber.split(",");
            if (splitedNumbers != null && splitedNumbers.length > 0) {
                List<String> selectedNumber = new ArrayList<>();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose phone number")
                        .setMultiChoiceItems(splitedNumbers, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    selectedNumber.add(splitedNumbers[which]);
                                } else if (selectedNumber.contains(splitedNumbers[which])) {
                                    selectedNumber.remove(splitedNumbers[which]);
                                }
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("smsto:"));
                                intent.setType("vnd.android-dir/mms-sms");
                                intent.putExtra("address", StringUtils.join(";", selectedNumber));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("smsto:"));
                intent.setType("vnd.android-dir/mms-sms");
                intent.putExtra("address", phoneNumber);
                startActivity(intent);
            }
        }
    }

    private void exportContact(PhoneContact phoneContact) {
        if (phoneContact != null) {
            if (contactViewModel.isExternalStorageWritable()) {
                List<String> exportContactIds = new ArrayList<>(Arrays.asList(new String[]{phoneContact.id}));
                String exportPath = contactViewModel.exportPhoneContacts(this, exportContactIds);
                Toast.makeText(this, "Exported successfully at " + exportPath, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "External storage is not available to write data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareContact(PhoneContact phoneContact) {
        if (phoneContact != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(ContactsContract.Contacts.CONTENT_VCARD_TYPE);
            Uri shareUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, phoneContact.lookup);
            intent.putExtra(Intent.EXTRA_STREAM, shareUri);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share a contact");
            startActivity(intent);
        }
    }

    private void removeContact(PhoneContact phoneContact) {
        if (phoneContact != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you sure?")
                    .setMessage("The contact will be removed permanently and can't be restored.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            List<String> deletedContactIds = new ArrayList<>(Arrays.asList(new String[]{ phoneContact.id }));
                            boolean result = contactViewModel.deletePhoneContacts(ContactListActivity.this, deletedContactIds);
                            if (result) {
                                refreshView();
                                Toast.makeText(ContactListActivity.this,"Deleted successfully contact " + phoneContact.name, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private boolean requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtils.hasPermissions(this, DeviceSetting.PERMISSIONS_MAKE_CALL)) {
                requestPermissions(DeviceSetting.PERMISSIONS_MAKE_CALL, DeviceSetting.PERMISSIONS_REQUEST_CODE);
                return false;
            }
            if (!PermissionUtils.hasPermissions(this, DeviceSetting.PERMISSIONS_EXPORT_CONTACT)) {
                requestPermissions(DeviceSetting.PERMISSIONS_EXPORT_CONTACT, DeviceSetting.PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == StopTrackingSetting.PERMISSIONS_REQUEST_CODE
                || requestCode == DeviceSetting.PERMISSIONS_REQUEST_CODE) {
            //TODO
        }
    }
}
