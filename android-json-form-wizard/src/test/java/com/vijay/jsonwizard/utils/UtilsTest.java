package com.vijay.jsonwizard.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.rules.RuleConstant;
import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UtilsTest extends BaseTest {

    @Mock
    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetFormConfigShouldReturnFormConfig() throws IOException, JSONException {
        Context context = Mockito.mock(Context.class);
        AssetManager assetManager = Mockito.mock(AssetManager.class);
        String configFileContent = "[{\"form_name\":\"anc_quick_check\",\"hidden_fields\":[],\"disabled_fields\":[\"leg_cramps\"]}]";
        Mockito.when(assetManager.open("json.form.config.json")).thenReturn(new ByteArrayInputStream(configFileContent.getBytes()));
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        JSONObject jsonResult = Utils.getFormConfig("anc_quick_check", "json.form.config.json", context);
        Assert.assertNotNull(jsonResult);
        Assert.assertEquals(3, jsonResult.length());
        Assert.assertTrue(jsonResult.has("form_name"));
        Assert.assertTrue(jsonResult.has("hidden_fields"));
        Assert.assertTrue(jsonResult.has("disabled_fields"));

    }

    @Test
    public void testConvertJsonArrayToSetShouldReturnASet() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("test");
        jsonArray.put("tester");
        jsonArray.put("tested");
        jsonArray.put("testing");
        jsonArray.put("test");
        Set<String> strings = Utils.convertJsonArrayToSet(jsonArray);
        Assert.assertEquals(4, strings.size());
    }

    @Test
    public void testConvertJsonArrayToSetShouldReturnNull() {
        Assert.assertNull(Utils.convertJsonArrayToSet(null));
    }

    @Test
    public void testBuildRulesWithUniqueId() throws JSONException {
        JSONObject formElement = new JSONObject("{\"key\":\"date_larvae_collection\",\"type\":\"edit_text\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"Date of larvae collection\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-dynamic\":\"repeating_groups_calculation_rules.yml\"}}}}");
        Assert.assertNotNull(formElement);

        String uniqueId = "33d56473a1de41e9986f952337c664ee";
        Map<String, List<Map<String, Object>>> rulesFileMap = new HashMap<>();

        Utils.buildRulesWithUniqueId(formElement, uniqueId, JsonFormConstants.CALCULATION, RuntimeEnvironment.application, rulesFileMap);
        Assert.assertNotNull(rulesFileMap);
        Assert.assertEquals(1, rulesFileMap.size());
        Assert.assertEquals("step2_larvae_total != ''", rulesFileMap.get("rule/repeating_groups_calculation_rules.yml").get(0).get("condition"));
    }

    @Test
    public void testBuildRulesWithUniqueIdWithoutRulesEngine() throws JSONException {
        JSONObject formElement = new JSONObject("{\"key\":\"task_business_status\",\"label\":\"Status\",\"type\":\"native_radio\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"options\":[{\"key\":\"In Progress\",\"text\":\"In Progress\"},{\"key\":\"Incomplete\",\"text\":\"Incomplete\"},{\"key\":\"Not Eligible\",\"text\":\"Not Eligible\"},{\"key\":\"Complete\",\"text\":\"Complete\"}],\"relevance\":{\"step2:moz_type\":{\"ex-checkbox\":[{\"or\":[\"An. funestus\"]}]}},\"v_required\":{\"value\":true,\"err\":\"Please specify the task status\"}}");
        Assert.assertNotNull(formElement);

        String uniqueId = "33d56473a1de41e9986f952337c664ee";
        Map<String, List<Map<String, Object>>> rulesFileMap = new HashMap<>();

        Utils.buildRulesWithUniqueId(formElement, uniqueId, JsonFormConstants.RELEVANCE, RuntimeEnvironment.application, rulesFileMap);
        Assert.assertNotNull(rulesFileMap);
        Assert.assertEquals(0, rulesFileMap.size());
    }

    @Test
    public void testCreateExpansionPanelChildren() throws JSONException {
        JSONArray fields = new JSONArray("[{\"key\":\"blood_type_test_status\",\"type\":\"extended_radio_button\",\"label\":\"Blood type test\",\"index\":0,\"values\":[\"done_today:Done today\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"blood_type_test_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"blood_type_test_date_today_hidden\",\"type\":\"hidden\",\"label\":\"\",\"index\":2,\"values\":[\"10-03-2020\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}},{\"key\":\"blood_type_test_date\",\"type\":\"date_picker\",\"label\":\"Blood type test date\",\"index\":3,\"values\":[\"10-03-2020\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163724AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}},{\"key\":\"blood_type\",\"type\":\"native_radio\",\"label\":\"Blood type\",\"index\":4,\"values\":[\"ab:AB\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"blood_type\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163117AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"rh_factor\",\"type\":\"native_radio\",\"label\":\"Rh factor\",\"index\":5,\"values\":[\"positive:Positive\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160232AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"rh_factor\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}]");
        Assert.assertNotNull(fields);

        List<String> strings = new Utils().createExpansionPanelChildren(fields);
        Assert.assertNotNull(strings);
        Assert.assertEquals(4, strings.size());
        Assert.assertEquals("Blood type test date:10-03-2020", strings.get(1));
    }

    @Test
    public void testGetDurationWithYears() {
        String duration = Utils.getDuration("10-03-2012", "30-05-2020");
        Assert.assertNotNull(duration);
        Assert.assertEquals("8y 2m", duration);
    }

    @Test
    public void testGetDurationWithWeeks() {
        String duration = Utils.getDuration("10-03-2020", "30-05-2020");
        Assert.assertNotNull(duration);
        Assert.assertEquals("11w 4d", duration);
    }

    @Test
    public void testGetDurationWithDays() {
        String duration = Utils.getDuration("1992-09-19T03:00:00.000+03:00", "1992-09-19T23:23:10.100+03:00");
        Assert.assertNotNull(duration);
        Assert.assertEquals("0d", duration);
    }

    @Test
    public void testBuildRulesWithUniqueIdShouldUpdateRelevanceRulesEngineObjectAccordingly() throws JSONException, IOException {
        String ruleType = "relevance";
        JSONObject element = new JSONObject();
        element.put(ruleType, new JSONObject("{\"rules-engine\":{\"ex-rules\":{\"rules-dynamic\":\"diagnose_and_treat_relevance.yml\"}}}"));
        String unique_id = "c29afdf9-843e-4c90-9a79-3dafd70e045b";
        AssetManager assetManager = Mockito.mock(AssetManager.class);
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        String contentOfRelevanceFile = "---\n" +
                "name: step1_diagnostic_test_result_spinner\n" +
                "description: diagnostic_test_result_spinner\n" +
                "priority: 1\n" +
                "condition: \"step1_diagnostic_test == 'Pregnancy Test' || step1_diagnostic_test == 'Malaria test'\n" +
                "|| step1_diagnostic_test == 'HIV test' || step1_diagnostic_test == 'Syphilis test'\n" +
                "|| step1_diagnostic_test == 'Hep B test' || step1_diagnostic_test == 'Hep C test'\n" +
                " || step1_diagnostic_test == 'TB Screening' || step1_diagnostic_test == 'Midstream urine Gram-staining'\"\n" +
                "actions:\n" +
                "  - \"isRelevant = true\"\n" +
                "---\n" +
                "name: step1_diagnostic_test_result_specify\n" +
                "description: diagnostic_test_result_specify\n" +
                "priority: 1\n" +
                "condition: \"step1_diagnostic_test == 'Ultra sound'\"\n" +
                "actions:\n" +
                "  - \"isRelevant = true\"\n" +
                "---\n" +
                "name: step1_diagnostic_test_result_glucose\n" +
                "description: diagnostic_test_result_glucose\n" +
                "priority: 1\n" +
                "condition: \"step1_diagnostic_test.startsWith('Blood Glucose test')\"\n" +
                "actions:\n" +
                "  - \"isRelevant = true\"\n" +
                "---\n" +
                "name: step1_diagnostic_test_result_spinner_blood_type\n" +
                "description: diagnostic_test_result_spinner_blood_type\n" +
                "priority: 1\n" +
                "condition: \"step1_diagnostic_test == 'Blood Type test'\"\n" +
                "actions:\n" +
                "  - \"isRelevant = true\"";
        InputStream inputStream = new ByteArrayInputStream(contentOfRelevanceFile.getBytes());
        Mockito.when(assetManager.open("rule/diagnose_and_treat_relevance.yml")).thenReturn(inputStream);
        Map<String, List<Map<String, Object>>> rulesFileMap = new HashMap<>();
        Utils.buildRulesWithUniqueId(element, unique_id, ruleType, context, rulesFileMap);
        JSONObject jsonExpectedObject = element.getJSONObject(ruleType);//new JSONObject(element);
        JSONArray jsonArray = jsonExpectedObject.optJSONObject(RuleConstant.RULES_ENGINE)
                .optJSONObject(JsonFormConstants.JSON_FORM_KEY.EX_RULES)
                .optJSONArray(RuleConstant.RULES_DYNAMIC);
        String resultKeyValue = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.optJSONObject(i).has(JsonFormConstants.KEY)) {
                resultKeyValue = jsonArray.optJSONObject(i).optString(JsonFormConstants.KEY);
                break;
            }
        }
        Assert.assertEquals(unique_id, resultKeyValue);
    }

    @Test
    public void testBuildRulesWithUniqueIdShouldUpdateRelevanceInlineObjectAccordingly() throws JSONException {
        String ruleType = "relevance";
        JSONObject element = new JSONObject();
        element.put(ruleType, new JSONObject("{\"step1:dob_unknown\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"false\\\")\"}}"));
        String unique_id = "c29afdf9-843e-4c90-9a79-3dafd70e045b";
        Map<String, List<Map<String, Object>>> rulesFileMap = new HashMap<>();
        Utils.buildRulesWithUniqueId(element, unique_id, ruleType, context, rulesFileMap);
        String expected = "{\"relevance\":{\"step1:dob_unknown_c29afdf9-843e-4c90-9a79-3dafd70e045b\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"false\\\")\"}}}";
        Assert.assertEquals(expected, element.toString());
    }

    @Test
    public void testShowProgressDialogShouldReturnIfCurrentProgressDialogIsShowingOrNull() {
        ProgressDialog progressDialog = Mockito.mock(ProgressDialog.class);
        Mockito.doReturn(true).when(progressDialog).isShowing();

        ReflectionHelpers.setStaticField(Utils.class, "progressDialog", progressDialog);
        Utils.showProgressDialog(R.string.please_wait_title, R.string.please_wait, null);
        Assert.assertEquals(progressDialog, ReflectionHelpers.getStaticField(Utils.class, "progressDialog"));
    }

    @Test
    public void testShowProgressDialogShouldCreateProgressDialog() {
        Assert.assertNull(ReflectionHelpers.getStaticField(Utils.class, "progressDialog"));

        Utils.showProgressDialog(R.string.hello_world, R.string.hello_world, RuntimeEnvironment.application);
        ProgressDialog progressDialog = ReflectionHelpers.getStaticField(Utils.class, "progressDialog");
        Assert.assertTrue(progressDialog.isShowing());
    }

    @Test
    public void testProcessNumberValuesShouldReturnCorrectValues() {
        Utils utils = new Utils();
        Assert.assertEquals(String.valueOf(4.7), utils.processNumberValues("4.7"));//test float
        Assert.assertEquals(String.valueOf(0.05), utils.processNumberValues("0.047"));//test rounding off
        Assert.assertEquals(47, utils.processNumberValues("47"));//test integer
        Assert.assertEquals(String.valueOf(Long.MAX_VALUE), utils.processNumberValues(String.valueOf(Long.MAX_VALUE)));//test when exception
    }

    @Test
    public void testGetDateFromStringShouldReturnCorrectDate() throws ParseException {
        String date = "20-12-1997";
        Assert.assertEquals(DatePickerFactory.DATE_FORMAT.parse(date), Utils.getDateFromString("20-12-1997"));
        Assert.assertNull(Utils.getDateFromString("20/12/1997"));
        Assert.assertNull(Utils.getDateFromString(""));
    }

    @Test
    public void testGetStringFromDateShouldReturnCorrectString() throws ParseException {
        String strDate = "20-12-1997";
        Date date = DatePickerFactory.DATE_FORMAT.parse(strDate);
        Assert.assertEquals(strDate, Utils.getStringFromDate(date));
        Assert.assertNull(Utils.getStringFromDate(null));
    }

    @Test
    public void testReverseDateStringShouldReturnReverseDateString() {
        String date = "20-12-1997";
        Assert.assertEquals("1997-12-20", Utils.reverseDateString(date, "-"));
    }

    @Test
    public void testGetStringValueShouldReturnStringValue() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.VALUES, new JSONArray().put("yes").put("no").put("don't know"));
        Utils utils = new Utils();
        String expected = "yes, no, don't know";
        Assert.assertEquals(expected, WhiteboxImpl.invokeMethod(utils, "getStringValue", jsonObject));
    }
}