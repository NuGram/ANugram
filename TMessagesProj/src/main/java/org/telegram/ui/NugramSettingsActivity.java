package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class NugramSettingsActivity extends BaseFragment {

    private static final String PREF_KEY = "nugram_settings_enabled";
    private static final String PREF_KEY_FILTER_ZALGO = "nugram_filter_zalgo";

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CHECK = 1;
    private static final int VIEW_TYPE_INFO = 2;

    private RecyclerListView listView;
    private ListAdapter listAdapter;

    private int rowCount;
    private int headerRow;
    private int enableRow;
    private int filterZalgoRow;
    private int infoRow;

    private boolean nugramEnabled;
    private boolean filterZalgoEnabled;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString(R.string.NugramSettings));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        nugramEnabled = MessagesController.getGlobalMainSettings().getBoolean(PREF_KEY, false);
        filterZalgoEnabled = MessagesController.getGlobalMainSettings().getBoolean(PREF_KEY_FILTER_ZALGO, true);
        updateRows();

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(listAdapter = new ListAdapter());
        listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            if (view instanceof TextCheckCell) {
                if (position == enableRow) {
                    nugramEnabled = !nugramEnabled;
                    MessagesController.getGlobalMainSettings().edit().putBoolean(PREF_KEY, nugramEnabled).apply();
                    ((TextCheckCell) view).setChecked(nugramEnabled);
                } else if (position == filterZalgoRow) {
                    filterZalgoEnabled = !filterZalgoEnabled;
                    MessagesController.getGlobalMainSettings().edit().putBoolean(PREF_KEY_FILTER_ZALGO, filterZalgoEnabled).apply();
                    ((TextCheckCell) view).setChecked(filterZalgoEnabled);
                }
            }
        });

        return fragmentView;
    }

    private void updateRows() {
        rowCount = 0;
        headerRow = rowCount++;
        enableRow = rowCount++;
        filterZalgoRow = rowCount++;
        infoRow = rowCount++;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == enableRow || position == filterZalgoRow;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == VIEW_TYPE_HEADER) {
                view = new HeaderCell(parent.getContext());
                view.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == VIEW_TYPE_CHECK) {
                view = new TextCheckCell(parent.getContext());
                view.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
            } else {
                view = new TextInfoPrivacyCell(parent.getContext());
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_HEADER: {
                    HeaderCell cell = (HeaderCell) holder.itemView;
                    cell.setText(LocaleController.getString(R.string.NugramSettings));
                    break;
                }
                case VIEW_TYPE_CHECK: {
                    TextCheckCell cell = (TextCheckCell) holder.itemView;
                    if (position == enableRow) {
                        cell.setTextAndCheck(LocaleController.getString(R.string.NugramSettingsToggle), nugramEnabled, true);
                    } else if (position == filterZalgoRow) {
                        cell.setTextAndCheck(LocaleController.getString(R.string.NugramFilterZalgo), filterZalgoEnabled, false);
                    }
                    break;
                }
                case VIEW_TYPE_INFO: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    cell.setText(LocaleController.getString(R.string.NugramSettingsInfo));
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == headerRow) {
                return VIEW_TYPE_HEADER;
            } else if (position == enableRow) {
                return VIEW_TYPE_CHECK;
            } else if (position == filterZalgoRow) {
                return VIEW_TYPE_CHECK;
            }
            return VIEW_TYPE_INFO;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }
    }
}
