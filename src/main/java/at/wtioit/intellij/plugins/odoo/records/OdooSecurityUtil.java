package at.wtioit.intellij.plugins.odoo.records;

import com.intellij.openapi.project.Project;
import java.util.*;

public class OdooSecurityUtil {
    public static class SecurityGroupInfo {
        public final String id;
        public final String filePath;
        public SecurityGroupInfo(String id, String filePath) {
            this.id = id;
            this.filePath = filePath;
        }
    }
    public static class AccessRuleInfo {
        public final String id;
        public final String[] row;
        public final String filePath;
        public AccessRuleInfo(String id, String[] row, String filePath) {
            this.id = id;
            this.row = row;
            this.filePath = filePath;
        }
    }
    // Groups: search via OdooGroupUtil
    public static Map<String, SecurityGroupInfo> findAllSecurityGroups(Project project) {
        Map<String, SecurityGroupInfo> result = new HashMap<>();
        for (var entry : OdooGroupUtil.findAllGroups(project).entrySet()) {
            result.put(entry.getKey(), new SecurityGroupInfo(entry.getKey(), entry.getValue().filePath));
        }
        return result;
    }
    public static SecurityGroupInfo findSecurityGroupById(Project project, String id) {
        var g = OdooGroupUtil.findGroupById(project, id);
        return g == null ? null : new SecurityGroupInfo(id, g.filePath);
    }
    // Access: search via OdooCsvRecordUtil
    public static Map<String, AccessRuleInfo> findAllAccessRules(Project project) {
        Map<String, AccessRuleInfo> result = new HashMap<>();
        for (var entry : OdooCsvRecordUtil.findAllCsvRecords(project).entrySet()) {
            result.put(entry.getKey(), new AccessRuleInfo(entry.getKey(), entry.getValue().row, entry.getValue().filePath));
        }
        return result;
    }
    public static AccessRuleInfo findAccessRuleById(Project project, String id) {
        var r = OdooCsvRecordUtil.findCsvRecordById(project, id);
        return r == null ? null : new AccessRuleInfo(id, r.row, r.filePath);
    }
    public static String generateAccessRuleTemplate(String id, String model, String group, String perm_read, String perm_write, String perm_create, String perm_unlink) {
        return "id,name,model_id:id,group_id:id,perm_read,perm_write,perm_create,perm_unlink\n" +
                id + "," + id + ",model_" + model + "," + group + "," + perm_read + "," + perm_write + "," + perm_create + "," + perm_unlink + "\n";
    }
} 