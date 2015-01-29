package de.dwslab.risk.gui.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLObjectVisitorAdapter;
import org.semanticweb.owlapi.util.OWLOntologyWalker;

import de.dwslab.risk.gui.exception.RoCAException;

public class OntologyBackgroundKnowledge implements BackgroundKnowledge {

    private static final IRI hasWeight =
            IRI.create("http://dwslab.de/riskmanagement/usecase-printer.owl#hasMlnWeight");

    private final Map<String, Predicate> predicates;
    private final Set<String> types;
    private final Map<String, Set<String>> entities;
    private final Map<Predicate, Set<Grounding>> groundings;

    public OntologyBackgroundKnowledge(Path ontology) {
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology owlOntology = manager.loadOntologyFromOntologyDocument(ontology.toFile());
            OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(owlOntology));
            predicates = new HashMap<>();
            types = new HashSet<>();
            entities = new HashMap<>();
            groundings = new HashMap<>();
            BackgroundKnowledgeVisitor visitor =
                    new BackgroundKnowledgeVisitor(predicates, types, entities, groundings);
            walker.walkStructure(visitor);
        } catch (OWLOntologyCreationException e) {
            throw new RoCAException("Cannot load background knowledge.", e);
        }
    }

    @Override
    public Map<String, Predicate> getPredicates() {
        return predicates;
    }

    @Override
    public Set<String> getTypes() {
        return types;
    }

    @Override
    public Map<String, Set<String>> getEntities() {
        return entities;
    }

    @Override
    public Map<Predicate, Set<Grounding>> getGroundings() {
        return groundings;
    }

    private static class BackgroundKnowledgeVisitor extends OWLObjectVisitorAdapter {

        private final Map<String, Predicate> predicates;
        private final Set<String> types;
        private final Map<String, Set<String>> entities;
        private final Map<Predicate, Set<Grounding>> groundings;

        public BackgroundKnowledgeVisitor(Map<String, Predicate> predicates, Set<String> types,
                Map<String, Set<String>> entities, Map<Predicate, Set<Grounding>> groundings) {
            this.predicates = predicates;
            this.types = types;
            this.entities = entities;
            this.groundings = groundings;
        }

        private String getName(IRI iri) {
            String iriString = iri.toString();
            int index = iriString.indexOf('#');
            if (index > -1) {
                return iriString.substring(index + 1, iriString.length());
            }
            index = iriString.lastIndexOf('/');
            if (index > -1) {
                return iriString.substring(index + 1, iriString.length());
            }
            return iriString;
        }

        @Override
        protected void handleDefault(OWLObject axiom) {
            // ignore it
            System.out.println("Ignored " + axiom);
        }

        @Override
        public void visit(OWLObjectPropertyAssertionAxiom axiom) {
            IRI iriSubject = axiom.getSubject().asOWLNamedIndividual().getIRI();
            IRI iriProperty = axiom.getProperty().asOWLObjectProperty().getIRI();
            IRI iriObject = axiom.getObject().asOWLNamedIndividual().getIRI();
            String subject = getName(iriSubject);
            String property = getName(iriProperty);
            String object = getName(iriObject);

            List<String> values = new ArrayList<>();
            values.add(subject);
            values.add(object);
            for (OWLAnnotation annotation : axiom.getAnnotations()) {
                if (annotation.getProperty().getIRI().equals(hasWeight)) {
                    String weight = annotation.getValue().asLiteral().toString();
                    values.add(weight);
                    break;
                }
            }

            // TODO types in der predicates map updaten
            // TODO entities updaten

            Predicate predicate = new Predicate(property);
            Grounding literal = new Grounding(predicate, values);
            Set<Grounding> literals = groundings.get(property);
            if (literals == null) {
                literals = new HashSet<>();
                groundings.put(predicate, literals);
            }
            literals.add(literal);
        }

        @Override
        public void visit(OWLClassAssertionAxiom axiom) {
            IRI iriClass = axiom.getClassExpression().asOWLClass().getIRI();
            IRI iriIndividual = axiom.getIndividual().asOWLNamedIndividual().getIRI();
            String theClass = getName(iriClass);
            String individual = getName(iriIndividual);

            List<String> values = new ArrayList<>();
            values.add(individual);
            for (OWLAnnotation annotation : axiom.getAnnotations()) {
                if (annotation.getProperty().getIRI().equals(hasWeight)) {
                    String weight = annotation.getValue().asLiteral().toString();
                    values.add(weight);
                    break;
                }
            }

            // TODO types in der predicates map updaten
            // TODO entities updaten

            Predicate predicate = new Predicate(theClass);
            Grounding literal = new Grounding(predicate, values);
            Set<Grounding> literals = groundings.get(theClass);
            if (literals == null) {
                literals = new HashSet<>();
                groundings.put(predicate, literals);
            }
            literals.add(literal);
        }

        @Override
        public void visit(OWLDataPropertyAssertionAxiom axiom) {
            IRI iriSubject = axiom.getSubject().asOWLNamedIndividual().getIRI();
            IRI iriProperty = axiom.getProperty().asOWLDataProperty().getIRI();
            OWLLiteral value = axiom.getObject().asLiteral().get();
            String subject = getName(iriSubject);
            String property = getName(iriProperty);
            String object = value.getLiteral();

            List<String> values = new ArrayList<>();
            values.add(subject);
            values.add(object);
            for (OWLAnnotation annotation : axiom.getAnnotations()) {
                if (annotation.getProperty().getIRI().equals(hasWeight)) {
                    String weight = annotation.getValue().asLiteral().toString();
                    values.add(weight);
                    break;
                }
            }

            // TODO types in der predicates map updaten
            // TODO entities updaten

            Predicate predicate = new Predicate(property);
            Grounding literal = new Grounding(predicate, values);
            Set<Grounding> literals = groundings.get(property);
            if (literals == null) {
                literals = new HashSet<>();
                groundings.put(predicate, literals);
            }
            literals.add(literal);
        }

        @Override
        public void visit(OWLClass ce) {
            String name = getName(ce.getIRI());
            types.add(name);
        }

        @Override
        public void visit(OWLObjectProperty property) {
            String name = getName(property.getIRI());
            Predicate predicate = new Predicate(false, name);
            predicates.put(name, predicate);
        }

        @Override
        public void visit(OWLDataProperty property) {
            String name = getName(property.getIRI());
            Predicate predicate = new Predicate(false, name);
            predicates.put(name, predicate);
        }

        @Override
        public void visit(OWLAnnotationProperty property) {
            String name = getName(property.getIRI());
            Predicate predicate = new Predicate(false, name);
            predicates.put(name, predicate);
        }

    }

}
