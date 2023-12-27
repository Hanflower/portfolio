package com.example.aram;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Big5Activity extends AppCompatActivity {
    ImageView close, question;
    Dialog big5Dialog;

    private boolean checkO = false;
    private boolean checkC = false;
    private boolean checkE = false;
    private boolean checkA = false;
    private boolean checkN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big5);

        close = findViewById(R.id.close);
        question = findViewById(R.id.ic_question);

        big5Dialog = new Dialog(Big5Activity.this);
        big5Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        big5Dialog.setContentView(R.layout.big5_dialog);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String userName = intent.getStringExtra("userName");

        TextView OCEAN_check = findViewById(R.id.OCEAN_check);
        OCEAN_check.setTextSize(20f);
        TextView OCEAN_description = findViewById(R.id.OCEAN_description);
        OCEAN_description.setTextSize(20f);
        TextView OCEAN_description2 = findViewById(R.id.OCEAN_description2);
        OCEAN_description2.setTextSize(15f);
        OCEAN_description2.setMovementMethod(new ScrollingMovementMethod());

        ProgressBar loadingbar = findViewById(R.id.progressBar);
        loadingbar.setVisibility(View.VISIBLE);

        //close 버튼 클릭시 창 꺼짐
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(Big5Activity.this, MainActivity.class));
                finish();
            }
        });

        question.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showBig5Dialog();
            }
        });

        // volley 연동
        // Openness 개방성 / Conscientiousness 성실성 / Extraversion 외향성 / Agreeableness 우호성 / Neuroticism 신경증
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject result = jsonObject.getJSONObject("big5");
                    loadingbar.setVisibility(View.GONE);

                    TextView titleText = findViewById(R.id.titleText);
                    titleText.setText(userName + "님의 특성분석 결과");

                    // 0622 graph
                    BarChart barChart = findViewById(R.id.OCEANgraph);
                    ArrayList<BarEntry> entry_chart = new ArrayList<>(); // 데이터를 담을 Arraylist

                    BarData barData = new BarData(); // 차트에 담길 데이터

                    // 데이터 받아서
                    int Oval = 0;
                    int Cval = 0;
                    int Eval = 0;
                    int Aval = 0;
                    int Nval = 0;
                    if (result.getString("userCharacterTypeO").equals("1")) { Oval = 1; }
                    else if (result.getString("userCharacterTypeO").equals("0")) { Oval = -1; }
                    if (result.getString("userCharacterTypeC").equals("1")) { Cval = 1; }
                    else if (result.getString("userCharacterTypeC").equals("0")) { Cval = -1; }
                    if (result.getString("userCharacterTypeE").equals("1")) { Eval = 1; }
                    else if (result.getString("userCharacterTypeE").equals("0")) { Eval = -1; }
                    if (result.getString("userCharacterTypeA").equals("1")) { Aval = 1; }
                    else if (result.getString("userCharacterTypeA").equals("0")) { Aval = -1; }
                    if (result.getString("userCharacterTypeN").equals("1")) { Nval = 1; }
                    else if (result.getString("userCharacterTypeN").equals("0")) { Nval = -1; }

                    entry_chart.add(new BarEntry(1, Oval)); //entry_chart에 좌표 데이터를 담는다.
                    entry_chart.add(new BarEntry(2, Cval));
                    entry_chart.add(new BarEntry(3, Eval));
                    entry_chart.add(new BarEntry(4, Aval));
                    entry_chart.add(new BarEntry(5, Nval));

                    BarDataSet barDataSet = new BarDataSet(entry_chart, "bardataset"); // 데이터가 담긴 Arraylist 를 BarDataSet 으로 변환한다.
                    barDataSet.setColor(getColor(R.color.serenity)); // 해당 BarDataSet 색 설정 :: 각 막대 과 관련된 세팅은 여기서 설정한다.
                    barData.addDataSet(barDataSet); // 해당 BarDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.
                    barData.setDrawValues(false);
                    barChart.setData(barData); // 차트에 위의 DataSet 을 넣는다.

                    barChart.getLegend().setEnabled(false);

                    barChart.getAxisLeft().setDrawZeroLine(true);
                    barChart.getAxisLeft().setZeroLineWidth(3f);
                    barChart.getAxisLeft().setZeroLineColor(getColor(R.color.serenity));

                    barChart.getAxisRight().setDrawGridLines(false);
                    barChart.getAxisLeft().setDrawGridLines(false);
                    barChart.getXAxis().setDrawGridLines(false);

                    barChart.getAxisRight().setDrawAxisLine(false);
                    barChart.getAxisLeft().setDrawAxisLine(false);
                    barChart.getXAxis().setDrawAxisLine(false);

                    Description description = new Description();
                    description.setEnabled(false);
                    barChart.setDescription(description);

                    barChart.getAxisLeft().setDrawLabels(false);
                    barChart.getAxisRight().setDrawLabels(false);

                    ArrayList<String> label = new ArrayList<>();
                    label.add("");
                    label.add("개방성");
                    label.add("성실성");
                    label.add("외향성");
                    label.add("우호성");
                    label.add("민감성");

                    barChart.getXAxis().setGranularity(1f);
                    barChart.getXAxis().setAxisMaximum(5.5f);
                    barChart.getXAxis().setAxisMinimum(0.5f);
                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(label));
                    barChart.getXAxis().setTextSize(70f);
                    barChart.getXAxis().setTextColor(getColor(R.color.classicBlue));
                    barChart.setExtraTopOffset(50f);

                    barChart.setMaxVisibleValueCount(5);
                    barChart.animateY(1000);
                    barChart.invalidate(); // 차트 업데이트
                    //barChart.setTouchEnabled(false); // 차트 터치 불가능하게
                    barChart.setScaleEnabled(false); // 확대 불가능

                    barDataSet.setHighLightAlpha(255);
                    barDataSet.setHighLightColor(getColor(R.color.primrose));


                    int finalOval = Oval;
                    int finalCval = Cval;
                    int finalEval = Eval;
                    int finalAval = Aval;
                    int finalNval = Nval;
                    barChart.setOnChartValueSelectedListener(
                            new OnChartValueSelectedListener() {
                                @Override
                                public void onValueSelected(Entry e, Highlight h) {
                                    String clickText = barChart.getXAxis().getValueFormatter().getAxisLabel(e.getX(), barChart.getXAxis());

                                    OCEAN_check.setText(clickText);
                                    if (clickText.equals("개방성")) {OCEAN_description.setText("열려있는 마음으로 \n새로운 것을 탐색하려는 성향");}
                                    else if (clickText.equals("성실성")) {OCEAN_description.setText("목표 지향적이고 꾸준한 노력으로 \n장기적인 성과를 추구하는 성향");}
                                    else if (clickText.equals("외향성")) {OCEAN_description.setText("주변 환경과 타인들과의 상호작용에서 \n개성을 표출하는 성향");}
                                    else if (clickText.equals("우호성")) {OCEAN_description.setText("타인과의 강한 유대를 형성하고\n친밀한 교류를 중시하는 성향");}
                                    else if (clickText.equals("민감성")) {OCEAN_description.setText("주변 환경의 변화와 자극에 \n민감하게 반응하는 성향");}


                                    if (clickText.equals("개방성")) {
                                        if (finalOval == 1) {
                                            OCEAN_description2.setText("개방성이 강한 사람은 새로운 경험과 아이디어에 열려있으며 창의적이고 유연한 사고를 갖고 있습니다. 예술, 음악 등 창의적 활동에 흥미를 느끼며, 다양한 분야에 흥미를 가지고 깊게 탐구하려는 경향이 있습니다. 여러 가지 아이디어나 관점을 결합해서 새로운 아이디어를 만들어내는 융합적 사고방식이 뛰어나나, 규칙이나 규제를 싫어하는 경향이 있습니다. \n" +
                                                    "새로운 경험과 아이디어에 집중하다가 과제를 완료하지 못하거나 일상적인 규칙이나 책임에 소홀할 수 있습니다. 목표 설정과 효과적인 시간 관리는 균형을 유지하는 데 도움이 됩니다. 종종 모든 아이디어를 긍정적으로만 받아들이기 쉬우므로 비판적 사고력을 길러 현실적이고 실용적인 결정을 내릴 수 있도록 해야 합니다. ");
                                        } else OCEAN_description2.setText("개방성이 약한 사람은 전통적이고 안정된 환경을 선호하며 새로운 아이디어나 변화에 대한 저항이 큽니다. 새로운 도전에 대한 두려움이 크고, 실패를 피하려고 합니다. 그러나 일관된 행동과 태도는 다른 사람들과의 관계에 신뢰를 구축하는 데 도움이 됩니다. 주어진 일에 몰두하고 끈기 있게 일할 수 있는 능력을 갖추고 있으며, 해당 분야에서 인정받는 전문가로 성장할 수 있습니다.\n" +
                                                "새로운 경험에 대한 두려움을 극복하기 위해 소소한 여행이나 다양한 취미를 통해 의도적으로 새로운 환경이나 활동에 참여해 보는 것이 중요합니다. 다양한 주제의 책이나 온라인 강의 또한 새로운 관심사 발견에 도움이 됩니다. 음악, 글쓰기 등 창의적인 활동에 참여함으로써 새로운 아이디어와 관점을 받아들이는 능력을 키울 수 있습니다.");
                                    }

                                    if (clickText.equals("성실성")) {
                                        if (finalCval == 1){
                                            OCEAN_description2.setText("성실성이 강한 사람은 큰 목표를 달성하기 위해 계획을 세우고 꾸준히 노력을 기울여 실천합니다. 자신의 감정과 행동을 잘 통제하며, 자신의 성과에 큰 가치를 두고 목표를 성취함으로써 자신에게 보상을 주는 경향이 있습니다. 하지만 완벽주의에 빠지기 쉬우며, 업무 효율성이 떨어질 수 있습니다. 계획이나 일정이 변경될 때 적응하기 어려울 수 있으며, 다른 사람에 대한 기대치로 협업 관계에서 갈등이 발생할 수 있습니다. \n" +
                                                    "업무와 개인 생활의 균형을 유지하면서도 효율적으로 일을 처리할 수 있는 시간 관리 방법을 익히고 실천하는 것이 중요합니다. 우선순위를 정하고 중요한 일을 먼저 처리하는 습관과 정기적으로 스트레스를 해소하는 시간을 갖는 것이 중요합니다. 열려있는 협력적인 자세를 취하고 계획에 유연성을 허용하는 자세가 필요합니다.");
                                        } else OCEAN_description2.setText("성실성이 약한 사람은 계획을 세우기 어려워 일정이나 목표를 놓칠 수 있습니다. 책임을 회피하려 하거나 어려운 일을 피하려는 경향이 있습니다. 그러나 보통 일을 더 느긋하게 받아들이는 경향이 있으며, 일을 처리하는 방식에서 자유로워 비교적 과도한 스트레스를 받지 않을 수 있습니다. 종종 피드백을 더 적극적으로 받아들이고 개선하려는 노력을 기울이는 경향이 있으며, 다양한 성격과 배경을 가진 사람들과 소통하기 쉬울 수 있습니다.  \n" +
                                                "자신의 일과 행동을 지속적으로 평가하고 개선할 수 있는 능력을 키우는 것이 중요합니다. 명확한 목표를 세우고 이를 달성하는 과정에서 지속적인 성취감을 느껴 자신감을 키우고 일의 방향성을 찾을 수 있습니다.");
                                    }

                                    if (clickText.equals("외향성")) {
                                        if (finalEval == 1){
                                            OCEAN_description2.setText("외향성이 강한 사람은 사람들과의 교류를 즐기며 활동적이고 사교적인 성향을 보입니다. 대화를 즐기며 주위의 사람들과 손쉽게 친분을 쌓을 수 있습니다. 사람들을 이끄는데 능숙하나, 때로는 과도한 감정 표현으로 인해 갈등이 생길 수 있습니다. 혼자 있는 시간을 즐기지 못하고 불안감을 느낄 수 있습니다. \n" +
                                                    "명상이나 필사 등 정적인 활동을 통해 자기통제 능력을 키우고 감정을 조절할 수 있는 기술을 향상시키는 것이 중요합니다. 주변 사람들에게 미치는 영향을 이해하며 타인에게 적절한 배려를 표현하는 것이 중요합니다. 내적 성장을 위한 시간에 투자하고 외부적인 자극에만 의존하지 않도록 하는 것이 중요합니다.");
                                        } else OCEAN_description2.setText("외향성이 약한 사람은 혼자 있는 시간을 즐기며 내적으로 탐구하는 성향을 보입니다. 자기 자신에 대한 독립성을 중요시하며, 복잡한 문제에 대해 집중력을 유지하는 능력이 있습니다. 주로 혼자 일하거나 자신이 책임을 질 때, 높은 책임감을 가지고 일을 처리하려고 합니다. 그러나 새로운 사람들과의 만남이나 사회적 상황에서 어색함을 느끼고 자신의 의견이나 요구를 적절하게 표현하지 못할 수 있습니다. \n" +
                                                "자신의 의견과 요구를 존중하고, 생각과 감정을 적절하게 표현하는 연습을 통해 자신의 자기주장을 더욱 강화하고 자신의 독립성을 높일 수 있습니다. 의도적으로 사회적 활동에 참여하고 새로운 사람들을 만나면서 소통 능력을 향상시킬 수 있습니다.");
                                    }

                                    if (clickText.equals("우호성")) {
                                        if (finalAval == 1){
                                            OCEAN_description2.setText("우호성이 강한 사람은 주변 사람들의 감정을 이해하고 공감하는 능력이 뛰어납니다. 갈등 상황에서 중재 역할을 하는 데 능숙할 수 있으며, 기꺼이 자기 자신을 희생하는 경향이 있어 타인에 대한 봉사 정신이 발달되어 있습니다. 타인에게 싫은 일을 부탁하거나 거절하는 것을 어려워할 수 있고, 때로는 자신의 감정이나 행동이 일관성 없이 변할 수 있습니다. \n" +
                                                    "자신의 가치에 대해 정확하게 이해하는 것이 중요합니다. 자기 존중감을 향상시키고, 자신의 의견을 정확하게 표현하고 솔직하게 대화하는 방법을 연습하면 다른 사람들과 깊은 관계를 형성할 수 있습니다. 다양한 의견을 수용하면서도 자신의 가치와 신념을 지키는 균형을 찾는 것은 건강한 관계를 형성하는데 중요합니다.");
                                        } else OCEAN_description2.setText("우호성이 약한 사람은 주변 환경에 크게 영향을 받지 않고 자신의 관심사나 취향에 집중할 수 있습니다. 객관적이고 논리적으로 상황을 판단할 수 있는 능력을 가지고 있으며, 이는 복잡한 문제를 분석하고 해결하는 데 도움이 됩니다. 거짓된 칭찬을 하지 않고 진실한 의견을 표현하는 경향이 있어 진정한 친구나 동료들과의 깊은 관계를 형성할 수 있습니다. 그러나 타인과 대화할 때 직설적으로 의견을 표현하고, 타인의 요구나 기대에 무관심한 태도를 취할 수 있습니다.  \n" +
                                                "상대방의 감정에 민감하게 반응하고, 대화 중에 타인의 의견에 귀를 기울이는 습관을 기르며 타인과의 이해관계를 더 강화할 수 있습니다. 또한, 적절하게 자신을 표현하는 법은 사회적 성장을 이끌 수 있는 기반이 될 것입니다.");
                                    }

                                    if (clickText.equals("민감성")) {
                                        if (finalNval == 1){
                                            OCEAN_description2.setText("민감성이 강한 사람은 일상적인 상황에서도 강한 강점을 느끼고, 작은 일에도 스트레스를 느낄 수 있습니다. 과거의 실수나 미래의 불확실성에 대한 지속적인 걱정을 갖기 쉽습니다. 그러나 세세한 변화에 주의를 기울일 수 있는 능력은 위기 대처 능력으로 이어질 수 있습니다. 강한 감정은 그림이나 글쓰기 등의 창작 활동에 도움 될 수 있습니다.\n" +
                                                    "자신의 의견을 확립하고 독립성을 키움으로써 다른 사람들과의 관계에서 민감성을 줄일 수 있습니다. 명상, 필사, 규칙적인 운동 등 스트레스 관리 기술을 습득하는 것은 도움이 됩니다. 가족이나 친구, 전문가와의 소통을 통해 감정적 지지를 받음으로써 안정성을 유지할 수 있습니다.");
                                        } else OCEAN_description2.setText("민감성이 약한 사람은 어려운 상황에서도 스트레스를 침착하게 대처하고 긍정적인 감정을 유지할 수 있습니다. 감정을 효과적으로 관리하고 주변 사람들에게 안정적인 환경을 제공할 수 있습니다. 때때로 부정적인 감정을 억제하고 숨기려는 경향이 있을 수 있습니다. 자신만의 안정된 상황 속에서 타인의 감정을 이해하고 공감하는 것이 어려울 수 있습니다. \n" +
                                                "자신의 감정을 열린 마음으로 표현하고 다양한 감정을 받아들일 수 있는 태도를 갖는 것이 중요합니다. 안정된 상태를 벗어나 새로운 환경이나 경험에 계속해서 도전하면 감정적인 안정성을 유지하는 능력을 향상시킬 수 있습니다.");
                                    }

                                    // '강', '약' 강조 처리
                                    String content = OCEAN_description2.getText().toString();
                                    SpannableString spannableString = new SpannableString(content);
                                    spannableString.setSpan(new RelativeSizeSpan(1.5f), 5, 6, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    OCEAN_description2.setText(spannableString);

                                }

                                @Override
                                public void onNothingSelected() {

                                }
                            }
                    );


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        // 서버로 Volley를 이용해서 요청을 함
        Big5Request big5Request = new Big5Request(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Big5Activity.this);
        queue.add(big5Request);

    }

    public void showBig5Dialog(){
        big5Dialog.show();

        Button close_btn = big5Dialog.findViewById(R.id.closeBtn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                big5Dialog.dismiss();
            }
        });
    }
}