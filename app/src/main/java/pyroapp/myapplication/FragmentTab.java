package pyroapp.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class FragmentTab extends Fragment implements FragmentTabContract.View{

    private ListView listView;
    private FloatingActionButton fab;
    private String selectedDay;
    private CustomAdapter customAdapter;
    private TinyDB db;
    private FragmentTabPresenter mPresenter;
    private AlertDialog.Builder builder;
    private boolean isEdit = false;
    private Subject subject;
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedDay = getArguments().getString("selectedDay");
        db=new TinyDB(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        listView = view.findViewById(R.id.listView);
        fab = view.findViewById(R.id.fab);
        customAdapter = new CustomAdapter(requireContext(),R.layout.list_element,new ArrayList<Subject>());
        listView.setAdapter(customAdapter);

        mPresenter = new FragmentTabPresenter(requireContext(),db,customAdapter,selectedDay,this);
        customAdapter.setPresenter(mPresenter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createBaseDialog("Warning", "The fields are full");
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPresenter.getAllElements().size() == 8){
                    createBaseDialog("Warning", "The fields are full");
                } else {
                    isEdit = false;
                    position = -1;
                    createAddDialog("New Lesson","");

                }
            }
        });

        mPresenter.updateAdapter();
    }

    private void createBaseDialog(String title, String message){
        builder = new AlertDialog.Builder(requireContext());
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void createAddDialog(String title, String message){
        builder = new AlertDialog.Builder(requireContext());
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        builder.setTitle(title);
        builder.setMessage(message);
        TextView text =new TextView(requireContext());
        final EditText input =new EditText(requireContext());
        TextView text1 =new TextView(requireContext());
        final EditText input1=new EditText(requireContext());
        TextView text2 =new TextView(requireContext());
        final EditText input2 =new EditText(requireContext());
        input2.setInputType(InputType.TYPE_CLASS_DATETIME);
        text.setTextColor(getResources().getColor(R.color.colorAccent));
        text.setText("Subject");
        text1.setText("Classroom");
        text1.setTextColor(getResources().getColor(R.color.colorAccent));
        text2.setText("Time");
        text2.setTextColor(getResources().getColor(R.color.colorAccent));
        layout.addView(text);
        layout.addView(input);
        layout.addView(text1);
        layout.addView(input1);
        layout.addView(text2);
        layout.addView(input2);
        String positiveTitle = "Add";
        if(isEdit){
            input.setText(subject.getSubjectName());
            input1.setText(subject.getClassroom());
            input2.setText(subject.getTime());
            positiveTitle = "Edit";
        }
        builder.setView(layout);
        builder.setPositiveButton(positiveTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String temp=input.getText().toString();
                String temp1=input1.getText().toString();
                String temp2=input2.getText().toString();
                checkNewFields(temp,temp1,temp2);
            }
        });
        builder.show();
    }

    private void checkNewFields(String temp, String temp1, String temp2) {
        if(temp.length() == 0 || temp1.length() == 0 || temp2.length() == 0){
            final AlertDialog.Builder builderCheck=new AlertDialog.Builder(requireContext());
            builderCheck.setTitle("Warning");
            builderCheck.setMessage("All fields must be filled in");
            builderCheck.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog1, int which) {
                    dialog1.cancel();
                }
            });
            builderCheck.show();
        } else {
            if (isEdit){
                ArrayList<Object> list = mPresenter.getAllElements();
                list.remove(position);
                list.add(new Subject(temp,temp1,temp2));
                mPresenter.editListElements(list);
            } else {
                mPresenter.addElement(new Subject(temp,temp1,temp2));
            }
        }

    }


    private void createEditDialog(boolean isEdit, int position) {
        if (isEdit) {
            this.subject = (Subject) mPresenter.getElementByIndex(position);
            this.position = position;
            this.isEdit = true;
            createAddDialog("Edit Lesson","");
        } else {
            ArrayList<Object> elements = mPresenter.getAllElements();
            elements.remove(position);
            mPresenter.editListElements(elements);
        }
    }

    @Override
    public void showActionDialog(final int position) {
        final AlertDialog.Builder builderAction=new AlertDialog.Builder(requireContext());
        builderAction.setTitle("Select Action");
        builderAction.setMessage("Select an actio to edit this element.");
        builderAction.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createEditDialog(true,position);
            }
        });
        builderAction.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createEditDialog(false,position);
            }
        });
        builderAction.show();
    }
}
