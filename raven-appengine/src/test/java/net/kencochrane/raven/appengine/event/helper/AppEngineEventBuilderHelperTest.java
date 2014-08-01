package net.kencochrane.raven.appengine.event.helper;

import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy;
import mockit.*;
import net.kencochrane.raven.event.EventBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static mockit.Deencapsulation.setField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

public class AppEngineEventBuilderHelperTest {
    @Tested
    private AppEngineEventBuilderHelper eventBuilderHelper;
    @Injectable
    private EventBuilder mockEventBuilder;
    @Mocked("getCurrentEnvironment")
    private ApiProxy mockApiProxy;
    @Injectable
    private ApiProxy.Environment mockEnvironment;
    @Injectable
    private SystemProperty mockApplicationId;
    @Injectable
    private SystemProperty mockApplicationVersion;

    @BeforeMethod
    public void setUp() throws Exception {
        setField(SystemProperty.class, "applicationId", mockApplicationId);
        setField(SystemProperty.class, "applicationVersion", mockApplicationVersion);

        new NonStrictExpectations() {{
            ApiProxy.getCurrentEnvironment();
            result = mockEnvironment;
            mockEnvironment.getAttributes();
            result = Collections.emptyMap();
        }};
    }

    @Test
    public void ensureHostnameDefineByApiProxyEnvironment(
            @Injectable("d7b8f251-ebe1-446f-8549-2b37982bd548") final String mockHostname)
            throws Exception {
        new NonStrictExpectations() {{
            mockEnvironment.getAttributes();
            result = Collections.singletonMap("com.google.appengine.runtime.default_version_hostname", mockHostname);
        }};

        eventBuilderHelper.helpBuildingEvent(mockEventBuilder);

        new Verifications() {{
            String hostname;
            mockEventBuilder.setServerName(hostname = withCapture());
            assertThat(hostname, is(mockHostname));
        }};
    }

    @Test
    public void ensureApplicationVersionIsAddedAsTag(
            @Injectable("dc485fa3-fc23-4e6c-b374-0d05d248e5a5") final String version) throws Exception {
        new NonStrictExpectations() {{
            mockApplicationVersion.get();
            result = version;
        }};

        eventBuilderHelper.helpBuildingEvent(mockEventBuilder);

        new Verifications() {{
            List<String> tagNames = new LinkedList<String>();
            List<String> tagValues = new LinkedList<String>();
            mockEventBuilder.addTag(withCapture(tagNames), withCapture(tagValues));

            Map<String, String> tags = new HashMap<String, String>();
            for (int i = 0; i < tagNames.size(); i++) {
                String tagName = tagNames.get(i);
                tags.put(tagName, tagValues.get(i));
            }

            assertThat(tags, hasEntry("GAE Application Version", version));
        }};
    }

    @Test
    public void ensureApplicationIdIsAddedAsTag(
            @Injectable("50a180eb-1484-4a07-9e44-b60d394cad18") final String applicationId) throws Exception {
        new NonStrictExpectations() {{
            mockApplicationId.get();
            result = applicationId;
        }};

        eventBuilderHelper.helpBuildingEvent(mockEventBuilder);

        new Verifications() {{
            List<String> tagNames = new LinkedList<String>();
            List<String> tagValues = new LinkedList<String>();
            mockEventBuilder.addTag(withCapture(tagNames), withCapture(tagValues));

            Map<String, String> tags = new HashMap<String, String>();
            for (int i = 0; i < tagNames.size(); i++) {
                String tagName = tagNames.get(i);
                tags.put(tagName, tagValues.get(i));
            }

            assertThat(tags, hasEntry("GAE Application Id", applicationId));
        }};
    }
}
