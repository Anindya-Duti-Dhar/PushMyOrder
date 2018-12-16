package duti.com.pushmyorder.library;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;

import java.util.ArrayList;

import duti.com.pushmyorder.R;

/**
 * Created by Duti on 12/16/2018.
 */

public class ItemList {

    boolean mOnceAnimate = false;
    DroidTool dt;

    public ItemList(DroidTool droidTool) {
        dt = droidTool;
    }

    public interface onRecyclerViewItemDelete {
        void onItemDeleteClick(Object o, int pos);
    }

    public interface onRecyclerViewItemClick {
        void onItemRowClick(Object o, int pos);
    }

    public interface onRecyclerViewItemSwipe {
        boolean onItemRowClick(int pos);
    }

    public interface onRecyclerViewItemCheck {
        void onItemRowCheck(boolean isChecked, Object o, int pos);
    }

    private ArrayList<?> dataList;
    public RecyclerView recyclerView;
    public LinkAdapter adapter = null;
    private onRecyclerViewItemClick customListenerClick = null;
    private onRecyclerViewItemDelete customListenerDelete = null;
    private onRecyclerViewItemSwipe customListenerSwipe = null;
    private onRecyclerViewItemCheck customListenerCheck = null;

    public void setRecyclerViewItemClickListener(onRecyclerViewItemClick listener) {
        this.customListenerClick = listener;
    }

    public void setRecyclerViewItemDeleteListener(onRecyclerViewItemDelete listener) {
        this.customListenerDelete = listener;
    }

    public void setRecyclerViewItemSwipeListener(onRecyclerViewItemSwipe listener) {
        this.customListenerSwipe = listener;
    }

    public void setRecyclerViewItemCheckListener(onRecyclerViewItemCheck listener) {
        this.customListenerCheck = listener;
    }

    public RecyclerView set(int recyclerResId, ArrayList<?> data, int styleResId,
                            int deleteImageViewResId, int orientation, int drawableIcon) {
        RecyclerView recyclerView = (RecyclerView) ((Activity) dt.c).findViewById(recyclerResId);
        recyclerView.setLayoutManager(new LinearLayoutManager(dt.c, orientation, false));
        adapter = new LinkAdapter(dt, data, styleResId, deleteImageViewResId, drawableIcon);
        recyclerView.setAdapter(adapter);

        adapter.setClickResponseListener(new LinkAdapter.onAdapterItemClick() {
            @Override
            public void onItemRowClick(Object o, int pos) {
                if (customListenerClick != null) customListenerClick.onItemRowClick(o, pos);
            }
        });

        adapter.setDeleteResponseListener(new LinkAdapter.onAdapterItemDelete() {
            @Override
            public void onItemDeleteClick(Object o, int pos) {
                if (customListenerDelete != null) customListenerDelete.onItemDeleteClick(o, pos);
            }
        });

        dataList = data;
        this.recyclerView = recyclerView;
        this.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mOnceAnimate) {
                    mOnceAnimate = true;
                    animate();
                }
            }
        });

        // init swipe to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe
                showAlert(position);//show alert dialog
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(this.recyclerView); //bind swipe to recylcerview

        return this.recyclerView;
    }

    public RecyclerView set(int recyclerResId, ArrayList<?> data, int styleResId,
                            int deleteImageViewResId, int columnCount, int orientation, int drawableIcon) {
        RecyclerView recyclerView = (RecyclerView) ((Activity) dt.c).findViewById(recyclerResId);
        recyclerView.setHasFixedSize(false);
        //int spacing = 16; // 50px
        //boolean includeEdge = true;
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(columnCount, spacing, includeEdge));

        recyclerView.setLayoutManager(new GridLayoutManager(dt.c, columnCount, orientation, false));
        adapter = new LinkAdapter(dt, data, styleResId, deleteImageViewResId, drawableIcon);
        recyclerView.setAdapter(adapter);

        adapter.setClickResponseListener(new LinkAdapter.onAdapterItemClick() {
            @Override
            public void onItemRowClick(Object o, int pos) {
                if (customListenerClick != null) customListenerClick.onItemRowClick(o, pos);
            }
        });

        adapter.setDeleteResponseListener(new LinkAdapter.onAdapterItemDelete() {
            @Override
            public void onItemDeleteClick(Object o, int pos) {
                if (customListenerDelete != null) customListenerDelete.onItemDeleteClick(o, pos);
            }
        });

        adapter.setCheckboxResponseListener(new LinkAdapter.onAdapterItemCheck() {
            @Override
            public void onItemChecked(boolean isChecked, Object o, int pos) {
                if (customListenerCheck != null) customListenerCheck.onItemRowCheck(isChecked, o, pos);
            }
        });

        dataList = data;
        this.recyclerView = recyclerView;
        this.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mOnceAnimate) {
                    mOnceAnimate = true;
                    animate();
                }
            }
        });

        return this.recyclerView;
    }

    boolean mOnceAnimate3 = false;

    public interface onRecyclerViewItemDelete3 {
        void onItemDeleteClick3(Object o, int pos);
    }

    public interface onRecyclerViewItemClick3 {
        void onItemRowClick3(Object o, int pos);
    }

    private ArrayList<?> dataList3;
    public RecyclerView recyclerView3;
    public LinkAdapter adapter3 = null;
    private onRecyclerViewItemClick3 customListenerClick3 = null;
    private onRecyclerViewItemDelete3 customListenerDelete3 = null;

    public void setRecyclerViewItemClickListener(onRecyclerViewItemClick3 listener) {
        this.customListenerClick3 = listener;
    }

    public void setRecyclerViewItemDeleteListener(onRecyclerViewItemDelete3 listener) {
        this.customListenerDelete3 = listener;
    }

    public RecyclerView set3(int recyclerResId, ArrayList<?> data, int styleResId,
                             int deleteImageViewResId, int columnCount, int orientation, int drawableIcon) {
        RecyclerView recyclerView = (RecyclerView) ((Activity) dt.c).findViewById(recyclerResId);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(dt.c, columnCount, orientation, false));
        adapter3 = new LinkAdapter(dt, data, styleResId, deleteImageViewResId, drawableIcon);
        recyclerView.setAdapter(adapter3);

        adapter3.setClickResponseListener(new LinkAdapter.onAdapterItemClick() {
            @Override
            public void onItemRowClick(Object o, int pos) {
                if (customListenerClick3 != null) customListenerClick3.onItemRowClick3(o, pos);
            }
        });

        adapter3.setDeleteResponseListener(new LinkAdapter.onAdapterItemDelete() {
            @Override
            public void onItemDeleteClick(Object o, int pos) {
                if (customListenerDelete3 != null) customListenerDelete3.onItemDeleteClick3(o, pos);
            }
        });

        dataList3 = data;
        this.recyclerView3 = recyclerView;
        this.recyclerView3.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mOnceAnimate3) {
                    mOnceAnimate3 = true;
                    animate3();
                }
            }
        });

        return this.recyclerView3;
    }

    public void animate3() {
        new Spruce.SpruceBuilder(recyclerView3)
                .sortWith(new DefaultSort(100))
                .animateWith(DefaultAnimations.shrinkAnimator(recyclerView3, 500),
                        ObjectAnimator.ofFloat(recyclerView3, "translationY", recyclerView3.getWidth(), 0f).setDuration(500))
                .start();
    }

    public RecyclerView set(RecyclerView recyclerView, ArrayList<?> data, int styleResId,
                            int deleteImageViewResId, int columnCount, int orientation, int drawableIcon) {

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(dt.c, columnCount, orientation, false));
        adapter = new LinkAdapter(dt, data, styleResId, deleteImageViewResId, drawableIcon);
        recyclerView.setAdapter(adapter);

        adapter.setClickResponseListener(new LinkAdapter.onAdapterItemClick() {
            @Override
            public void onItemRowClick(Object o, int pos) {
                if(customListenerClick != null)customListenerClick.onItemRowClick(o, pos);
            }
        });

        adapter.setDeleteResponseListener(new LinkAdapter.onAdapterItemDelete() {
            @Override
            public void onItemDeleteClick(Object o, int pos) {
                if(customListenerDelete != null)customListenerDelete.onItemDeleteClick(o, pos);
            }
        });

        dataList = data;
        this.recyclerView = recyclerView;
        this.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mOnceAnimate) {
                    mOnceAnimate = true;
                    animate();
                }
            }
        });

        return this.recyclerView;
    }


    public void animate() {
        new Spruce.SpruceBuilder(recyclerView)
                .sortWith(new DefaultSort(100))
                .animateWith(DefaultAnimations.shrinkAnimator(recyclerView, 500),
                        ObjectAnimator.ofFloat(recyclerView, "translationY", recyclerView.getWidth(), 0f).setDuration(500))
                .start();
    }



    /************************ popup dialog recycler region start *********************/

    boolean mPopupOnceAnimate = false;

    public interface onPopupRecyclerViewItemDelete {
        void onItemDeleteClick(Object o, int pos);
    }

    public interface onPopupRecyclerViewItemClick {
        void onItemRowClick(Object o, int pos);
    }

    public interface onPopupRecyclerViewItemSwipe {
        boolean onItemRowClick(int pos);
    }

    public interface onPopupRecyclerViewItemCheck {
        void onItemRowCheck(boolean isChecked, Object o, int pos);
    }

    private ArrayList<?> popupDataList;
    public RecyclerView popupRecyclerView;
    public LinkAdapter popupAdapter = null;
    private onPopupRecyclerViewItemClick popupCustomListenerClick = null;
    private onPopupRecyclerViewItemDelete popupCustomListenerDelete = null;
    private onPopupRecyclerViewItemSwipe popupCustomListenerSwipe = null;
    private onPopupRecyclerViewItemCheck popupCustomListenerCheck = null;

    public void initList(RecyclerView recyclerView, ArrayList<?> data, int adapterLayoutResId,
                         int drawableDeleteIcon, int columnCount, int orientation, int drawableListIcon, onPopupRecyclerViewItemClick itemClickListener
            , onPopupRecyclerViewItemDelete itemDeleteListener, final onPopupRecyclerViewItemSwipe itemSwipeListener, final String deleteErrorMessage) {

        popupDataList = data;
        popupRecyclerView = recyclerView;

        popupRecyclerView.setHasFixedSize(false);
        popupRecyclerView.setLayoutManager(new GridLayoutManager(dt.c, columnCount, orientation, false));
        popupAdapter = new LinkAdapter(dt, data, adapterLayoutResId, drawableDeleteIcon, drawableListIcon);
        popupRecyclerView.setAdapter(popupAdapter);

        // item click listener
        popupCustomListenerClick = itemClickListener;

        popupAdapter.setClickResponseListener(new LinkAdapter.onAdapterItemClick() {
            @Override
            public void onItemRowClick(Object o, int pos) {
                if (popupCustomListenerClick != null)
                    popupCustomListenerClick.onItemRowClick(o, pos);
            }
        });

        // item delete listener
        popupCustomListenerDelete = itemDeleteListener;

        popupAdapter.setDeleteResponseListener(new LinkAdapter.onAdapterItemDelete() {
            @Override
            public void onItemDeleteClick(Object o, int pos) {
                if (popupCustomListenerDelete != null)
                    popupCustomListenerDelete.onItemDeleteClick(o, pos);
            }
        });

        // item swipe listener
        popupCustomListenerSwipe = itemSwipeListener;

        if (popupCustomListenerSwipe != null) {
            // init swipe to delete
            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition(); //get position which is swipe
                    showPopupListItemDeleteAlert(position, deleteErrorMessage);//show alert dialog
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(popupRecyclerView); //bind swipe to recylcer view
        }

        popupRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mPopupOnceAnimate) {
                    mPopupOnceAnimate = true;
                    popupAnimate();
                }
            }
        });
    }

    ///lll
    public void initList(RecyclerView recyclerView, ArrayList<?> data, int adapterLayoutResId,
                         int drawableDeleteIcon, int columnCount, int orientation, int drawableListIcon, onPopupRecyclerViewItemClick itemClickListener
            , onPopupRecyclerViewItemDelete itemDeleteListener, final onPopupRecyclerViewItemSwipe itemSwipeListener, onPopupRecyclerViewItemCheck itemCheckListener, final String deleteErrorMessage) {

        popupDataList = data;
        popupRecyclerView = recyclerView;

        popupRecyclerView.setHasFixedSize(false);
        popupRecyclerView.setLayoutManager(new GridLayoutManager(dt.c, columnCount, orientation, false));
        popupAdapter = new LinkAdapter(dt, data, adapterLayoutResId, drawableDeleteIcon, drawableListIcon);
        popupRecyclerView.setAdapter(popupAdapter);

        // item check listener
        popupCustomListenerCheck = itemCheckListener;

        popupAdapter.setCheckboxResponseListener(new LinkAdapter.onAdapterItemCheck() {
            @Override
            public void onItemChecked(boolean isChecked, Object o, int pos) {
                if (popupCustomListenerCheck != null)
                    popupCustomListenerCheck.onItemRowCheck(isChecked, o, pos);
            }
        });

        // item click listener
        popupCustomListenerClick = itemClickListener;

        popupAdapter.setClickResponseListener(new LinkAdapter.onAdapterItemClick() {
            @Override
            public void onItemRowClick(Object o, int pos) {
                if (popupCustomListenerClick != null)
                    popupCustomListenerClick.onItemRowClick(o, pos);
            }
        });

        // item delete listener
        popupCustomListenerDelete = itemDeleteListener;

        popupAdapter.setDeleteResponseListener(new LinkAdapter.onAdapterItemDelete() {
            @Override
            public void onItemDeleteClick(Object o, int pos) {
                if (popupCustomListenerDelete != null)
                    popupCustomListenerDelete.onItemDeleteClick(o, pos);
            }
        });

        // item swipe listener
        popupCustomListenerSwipe = itemSwipeListener;

        if (popupCustomListenerSwipe != null) {
            // init swipe to delete
            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition(); //get position which is swipe
                    showPopupListItemDeleteAlert(position, deleteErrorMessage);//show alert dialog
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(popupRecyclerView); //bind swipe to recylcer view
        }

        popupRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mPopupOnceAnimate) {
                    mPopupOnceAnimate = true;
                    popupAnimate();
                }
            }
        });
    }

    public void popupAnimate() {
        new Spruce.SpruceBuilder(popupRecyclerView)
                .sortWith(new DefaultSort(100))
                .animateWith(DefaultAnimations.shrinkAnimator(popupRecyclerView, 500),
                        ObjectAnimator.ofFloat(popupRecyclerView, "translationY", popupRecyclerView.getWidth(), 0f).setDuration(500))
                .start();
    }

    // alert for confirmation to delete
    public void showPopupListItemDeleteAlert(final int position, final String deleteErrorMessage) {

        dt.alert.showWarning(dt.gStr(R.string.want_to_delete));
        dt.alert.setAlertListener(new SweetAlert.AlertListener() {
            @Override
            public void onAlertClick(boolean isCancel) {
                // item swipe listener
                if (popupCustomListenerSwipe != null) {
                    if (!isCancel) {
                        if (!popupCustomListenerSwipe.onItemRowClick(position)) {
                            dt.alert.showError(dt.gStr(R.string.common_warning_title), deleteErrorMessage, dt.gStr(R.string.ok));
                            popupAdapter.notifyItemRemoved(position + 1);
                            popupAdapter.notifyItemRangeChanged(position, popupAdapter.getItemCount());
                        } else {
                            popupAdapter.notifyItemRemoved(position + 1);
                            popupAdapter.notifyItemRangeChanged(position, popupAdapter.getItemCount());
                        }
                    } else {
                        popupAdapter.notifyItemRemoved(position + 1);
                        popupAdapter.notifyItemRangeChanged(position, popupAdapter.getItemCount());
                    }
                }
            }
        });
    }


    /************************ popup dialog recycler region end *********************/

    /************************ popup dialog 2 recycler region start *********************/

    boolean mPopupOnceAnimate2 = false;

    public interface onPopupRecyclerViewItemDelete2 {
        void onItemDeleteClick2(Object o, int pos);
    }

    public interface onPopupRecyclerViewItemClick2 {
        void onItemRowClick2(Object o, int pos);
    }

    public interface onPopupRecyclerViewItemSwipe2 {
        boolean onItemRowClick2(int pos);
    }

    private ArrayList<?> popupDataList2;
    public RecyclerView popupRecyclerView2;
    public LinkAdapter popupAdapter2 = null;
    private onPopupRecyclerViewItemClick2 popupCustomListenerClick2 = null;
    private onPopupRecyclerViewItemDelete2 popupCustomListenerDelete2 = null;
    private onPopupRecyclerViewItemSwipe2 popupCustomListenerSwipe2 = null;

    public void initList2(RecyclerView recyclerView, ArrayList<?> data, int adapterLayoutResId,
                          int drawableDeleteIcon, int columnCount, int orientation, int drawableListIcon, onPopupRecyclerViewItemClick2 itemClickListener
            , onPopupRecyclerViewItemDelete2 itemDeleteListener, final onPopupRecyclerViewItemSwipe2 itemSwipeListener, final String deleteErrorMessage) {

        popupDataList2 = data;
        popupRecyclerView2 = recyclerView;

        popupRecyclerView2.setHasFixedSize(false);
        popupRecyclerView2.setLayoutManager(new GridLayoutManager(dt.c, columnCount, orientation, false));
        popupAdapter2 = new LinkAdapter(dt, data, adapterLayoutResId, drawableDeleteIcon, drawableListIcon);
        popupRecyclerView2.setAdapter(popupAdapter2);

        // item click listener
        popupCustomListenerClick2 = itemClickListener;

        popupAdapter2.setClickResponseListener(new LinkAdapter.onAdapterItemClick() {
            @Override
            public void onItemRowClick(Object o, int pos) {
                if (popupCustomListenerClick2 != null)
                    popupCustomListenerClick2.onItemRowClick2(o, pos);
            }
        });

        // item delete listener
        popupCustomListenerDelete2 = itemDeleteListener;

        popupAdapter2.setDeleteResponseListener(new LinkAdapter.onAdapterItemDelete() {
            @Override
            public void onItemDeleteClick(Object o, int pos) {
                if (popupCustomListenerDelete2 != null)
                    popupCustomListenerDelete2.onItemDeleteClick2(o, pos);
            }
        });

        // item swipe listener
        popupCustomListenerSwipe2 = itemSwipeListener;

        if (popupCustomListenerSwipe2 != null) {
            // init swipe to delete
            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition(); //get position which is swipe
                    showPopupListItemDeleteAlert2(position, deleteErrorMessage);//show alert dialog
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(popupRecyclerView2); //bind swipe to recylcer view
        }

        popupRecyclerView2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mPopupOnceAnimate2) {
                    mPopupOnceAnimate2 = true;
                    popupAnimate2();
                }
            }
        });
    }

    public void popupAnimate2() {
        new Spruce.SpruceBuilder(popupRecyclerView2)
                .sortWith(new DefaultSort(100))
                .animateWith(DefaultAnimations.shrinkAnimator(popupRecyclerView2, 500),
                        ObjectAnimator.ofFloat(popupRecyclerView2, "translationY", popupRecyclerView2.getWidth(), 0f).setDuration(500))
                .start();
    }

    // alert for confirmation to delete
    public void showPopupListItemDeleteAlert2(final int position, final String deleteErrorMessage) {

        dt.alert.showWarning(dt.gStr(R.string.want_to_delete));
        dt.alert.setAlertListener(new SweetAlert.AlertListener() {
            @Override
            public void onAlertClick(boolean isCancel) {
                // item swipe listener
                if (popupCustomListenerSwipe2 != null) {
                    if (!isCancel) {
                        if (!popupCustomListenerSwipe2.onItemRowClick2(position)) {
                            dt.alert.showError(dt.gStr(R.string.common_warning_title), deleteErrorMessage, dt.gStr(R.string.ok));
                            popupAdapter2.notifyItemRemoved(position + 1);
                            popupAdapter2.notifyItemRangeChanged(position, popupAdapter2.getItemCount());
                        } else {
                            popupAdapter2.notifyItemRemoved(position + 1);
                            popupAdapter2.notifyItemRangeChanged(position, popupAdapter2.getItemCount());
                        }
                    } else {
                        popupAdapter2.notifyItemRemoved(position + 1);
                        popupAdapter2.notifyItemRangeChanged(position, popupAdapter2.getItemCount());
                    }
                }
            }
        });
    }


    /************************ popup dialog recycler region end *********************/


    // alert for confirmation to update
    public void showAlert(final int position) {
        dt.alert.showWarning(dt.gStr(R.string.want_to_delete));
        dt.alert.setAlertListener(new SweetAlert.AlertListener() {
            @Override
            public void onAlertClick(boolean isCancel) {
                if (!isCancel) {
                    if (customListenerSwipe != null) {
                        if (!customListenerSwipe.onItemRowClick(position)) {
                            dt.alert.showError(dt.gStr(R.string.common_warning_title), dt.gStr(R.string.already_synced), dt.gStr(R.string.ok));
                            adapter.notifyItemRemoved(position + 1);
                            adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                        } else {
                            adapter.notifyItemRemoved(position + 1);
                            adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                        }
                    }
                } else {
                    adapter.notifyItemRemoved(position + 1);
                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                }
            }
        });
    }

    public void showHideNoRecordMessage(Context context, ArrayList<?> data, int recyclerResID, int resId) {
        TextView mNoDataMessage = dt.ui.textView.getObject(resId);
        RecyclerView mRecyclerView = dt.ui.recyclerView.getRes(recyclerResID);
        if (data.size() > 0) {
            if (mNoDataMessage.getVisibility() == View.VISIBLE) {
                mNoDataMessage.setVisibility(View.GONE);
            }
            if (mRecyclerView.getVisibility() == View.GONE) {
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            if (mNoDataMessage.getVisibility() == View.GONE) {
                mNoDataMessage.setVisibility(View.VISIBLE);
            }
            if (mRecyclerView.getVisibility() == View.VISIBLE) {
                mRecyclerView.setVisibility(View.GONE);
            }
        }
    }



}
