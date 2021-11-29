package kr.co.skhu.schedule;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<ScheduleItem> mScheduleItems;
    private Context mContext;
    private DBHelper mDBHelper;
    private DatePickerDialog.OnDateSetListener selectDateMethod;

    public CustomAdapter(ArrayList<ScheduleItem> mScheduleItems, Context mContext){
        this.mScheduleItems = mScheduleItems;
        this.mContext = mContext;
        mDBHelper = new DBHelper(mContext);
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item, parent, false);
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position){
        holder.main_title.setText(mScheduleItems.get(position).getTitle());
        holder.main_content.setText(mScheduleItems.get(position).getContent());
        holder.main_date.setText(mScheduleItems.get(position).getDate());

        if(mScheduleItems.get(position).getChecked()==1){
            holder.main_img.setImageResource(R.drawable.red_tie_cat);
        }else{
            holder.main_img.setImageResource(R.drawable.cat_blank);
        }
    }

    @Override
    public int getItemCount(){
        return mScheduleItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView main_title;
        private TextView main_content;
        private TextView main_date;
        private ImageButton main_cancelBtn;
        private ImageView main_img;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            main_title = itemView.findViewById(R.id.main_title);
            main_content = itemView.findViewById(R.id.main_content);
            main_date = itemView.findViewById(R.id.main_date);
            main_cancelBtn = itemView.findViewById(R.id.main_cancel);
            main_img = itemView.findViewById(R.id.main_img);

            itemView.setOnClickListener(new View.OnClickListener(){
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v){
                    int curPos = getAdapterPosition();
                    ScheduleItem scheduleItem = mScheduleItems.get(curPos);

                    Dialog dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    dialog.setContentView(R.layout.activity_detail);
                    EditText input_title = dialog.findViewById(R.id.detail_title);
                    TextView input_dDay = dialog.findViewById(R.id.detail_date);
                    EditText input_content = dialog.findViewById(R.id.detail_content);
                    Button ok_btn = dialog.findViewById(R.id.detail_ok);
                    Button cancel_btn = dialog.findViewById(R.id.detail_cancel);
                    ImageButton select_date = dialog.findViewById(R.id.date_btn);
                    CheckBox chk_detail = dialog.findViewById(R.id.detail_checkBox);

                    input_title.setText(scheduleItem.getTitle());
                    input_content.setText(scheduleItem.getContent());
                    input_dDay.setText(scheduleItem.getDate());

                    if(scheduleItem.getChecked() == 1){
                        chk_detail.setChecked(true);
                    }else{
                        chk_detail.setChecked(false);
                    }

                    Calendar cal = Calendar.getInstance();
                    int mYear = cal.get(Calendar.YEAR);
                    int mMonth = cal.get(Calendar.MONTH);
                    int mDate = cal.get(Calendar.DAY_OF_MONTH);

                    select_date.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onClick(View v) {
                            selectDateMethod = new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    input_dDay.setText(year + "년" + (month+1) + "월" + dayOfMonth + "일");
                                }
                            };

                            DatePickerDialog dialogDate = new DatePickerDialog(mContext, selectDateMethod, mYear, mMonth, mDate);
                            dialogDate.show();
                            dialogDate.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(R.color.black);
                            dialogDate.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(R.color.black);
                        }
                    });

                    cancel_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();;
                        }
                    });

                    ok_btn.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            String title = input_title.getText().toString();
                            String content = input_content.getText().toString();
                            String new_date = input_dDay.getText().toString();
                            String beforeDate = scheduleItem.getDate();

                            int checked;

                            if(chk_detail.isChecked()){
                                checked=1;
                            }else{
                                checked = 0;
                            }

                            mDBHelper.UpdateSchedule(checked, title, content, new_date, beforeDate);

                            scheduleItem.setTitle(title);
                            scheduleItem.setContent(content);
                            scheduleItem.setDate(new_date);
                            scheduleItem.setChecked(checked);

                            notifyItemChanged(curPos, scheduleItem);
                            dialog.dismiss();

                            Toast toast_upgrade = Toast.makeText(mContext, "일정 수정이 완료되었습니다.", Toast.LENGTH_SHORT);
                            toast_upgrade.setGravity(Gravity.BOTTOM, 0, 0);
                            toast_upgrade.show();


                            /*Toast.makeText(mContext, "일정 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();*/
                        }
                    });
                    dialog.show();
                }
            });

            main_cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int curPos = getAdapterPosition();
                    ScheduleItem scheduleItem = mScheduleItems.get(curPos);
                    int id = scheduleItem.getId();
                    mDBHelper.DeleteSchedule(id);

                    mScheduleItems.remove(curPos);
                    notifyItemRemoved(curPos);

                    Toast toast_delete = Toast.makeText(mContext, "일정 삭제가 완료되었습니다.", Toast.LENGTH_SHORT);
                    toast_delete.setGravity(Gravity.BOTTOM, 0, 0);
                    toast_delete.show();
                    /*Toast.makeText(mContext, "일정 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();*/
                }
            });
        }
    }
    public void addItem(ScheduleItem _item){
        mScheduleItems.add(0, _item);
        notifyItemInserted(0);
    }
}