package de.dwslab.risk.gui.jgraphx.model;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.parser.SyntaxReader;

import de.dwslab.risk.gui.exception.RoCAException;

public class MlnBackgroundKnowledge implements BackgroundKnowledge {

    public MlnBackgroundKnowledge(String mln, String evidence) {
        try {
            SyntaxReader reader = new SyntaxReader();
            Model model = reader.getModel(mln, evidence);

            model.getAllHiddenPredicates();
            model.getAllObservedPredicates();
            model.getAllTypes();
            model.getFormulas();

        } catch (Exception e) {
            throw new RoCAException("Cannot load background knowledge.", e);
        }
    }

    private static Map<String, Predicate> getPredicates(String mln) throws Exception {
        Map<String, Predicate> map = new HashMap<>();

        Files.lines(Paths.get(mln))
                .filter(s -> !s.isEmpty() && !s.startsWith("//") && !s.contains("v")
                        && !s.contains("\""))
                .map(s -> s.trim())
                .map(s -> (s.startsWith("*") ? s.substring(1) : s))
                .forEach(
                        p -> {
                            if ((p.length() > 0) && !p.startsWith("//")
                                    && (!p.contains("v") && !p.contains("\""))) {
                                boolean observed = false;
                                if (p.startsWith("*")) {
                                    observed = true;
                                    p = p.substring(1);
                                }

                                String name = p.substring(0, p.indexOf("(")).trim();

                                List<String> types = new ArrayList<>();
                                Pattern pattern = Pattern.compile("([^\\s(),]+)");
                                Matcher matcher = pattern.matcher(p.substring(p.indexOf("(")));
                                while (matcher.find()) {
                                    types.add(matcher.group(1));
                                }

                                Predicate pred = new Predicate(observed, name, types);
                                map.put(name, pred);
                            }

                        });

        return map;
    }
}
