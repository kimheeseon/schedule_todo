package kr.co.skhu.schedule;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv_schedule;
    private ImageButton btn_add;
    private ArrayList<ScheduleItem> scheduleItems;
    private DBHelper mDBHelper;
    private CustomAdapter mAdapter;
    private DatePickerDialog.OnDateSetListener callbackMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setInit();
    }

    private void setInit() {
        mDBHelper = new DBHelper(this);
        rv_schedule = findViewById(R.id.rv_schedule);
        btn_add = findViewById(R.id.plusBtn);
        scheduleItems = new ArrayList<>();

        loadRecentDB();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Material_Light_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.activity_detail);
                EditText input_title = dialog.findViewById(R.id.detail_title);
                EditText input_content = dialog.findViewById(R.id.detail_content);
                ImageButton select_date = dialog.findViewById(R.id.date_btn);
                TextView input_dDay = dialog.findViewById(R.id.detail_date);
                Button ok_btn = dialog.findViewById(R.id.detail_ok);
                Button cancel_btn = dialog.findViewById(R.id.detail_cancel);
                CheckBox detail_check = dialog.findViewById(R.id.detail_checkBox);

                Calendar cal = Calendar.getInstance();
                int mYear = cal.get(Calendar.YEAR);
                int mMonth = cal.get(Calendar.MONTH);
                int mDate = cal.get(Calendar.DAY_OF_MONTH);
                input_dDay.setText(mYear +"년"+ (mMonth+1) +"월"+ mDate + "일");

                select_date.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(View v) {
                        callbackMethod = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                input_dDay.setText(year + "년" + (month+1) + "월" + dayOfMonth + "일");
                            }
                        };
                        DatePickerDialog dialogDate = new DatePickerDialog(input_dDay.getContext(), callbackMethod, mYear, mMonth, mDate);
                        dialogDate.show();
                        dialogDate.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(R.color.black);
                        dialogDate.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(R.color.black);
                    }
                });

                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int test_check;
                        if(detail_check.isChecked()){
                            test_check = 1;
                        }else{
                            test_check = 0;
                        }

                        mDBHelper.InsertSchedule(test_check, input_title.getText().toString(), input_content.getText().toString(), input_dDay.getText().toString());

                        ScheduleItem item = new ScheduleItem();
                        item.setTitle(input_title.getText().toString());
                        item.setContent(input_content.getText().toString());
                        item.setDate(input_dDay.getText().toString());
                        item.setChecked(test_check);

                        mAdapter.addItem(item);
                        rv_schedule.scrollToPosition(0);

                        dialog.dismiss();

                        Toast toast_insert = Toast.makeText(getApplicationContext(), "일정 추가가 완료되었습니다.", Toast.LENGTH_SHORT);
                        toast_insert.setGravity(Gravity.BOTTOM, 0, 0);
                        toast_insert.show();


                       /* Toast.makeText(MainActivity.this, "일정 추가가 완료되었습니다.", Toast.LENGTH_SHORT).show();*/
                    }
                });

                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void loadRecentDB() {
        scheduleItems = mDBHelper.getSchedule();
        if(mAdapter == null){
            mAdapter = new CustomAdapter(scheduleItems, this);
            rv_schedule.setHasFixedSize(true);
            rv_schedule.setAdapter(mAdapter);
        }
    }
}