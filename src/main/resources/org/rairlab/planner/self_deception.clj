{:definitions
 {:name       "belief intro"
  :background [(forall [?p] (if (Proposition ?p) (or (True ?p) (False ?p))))
               (forall [?p] (iff (True ?p) (not (False ?p))))
               (forall [?p] (if (True ?p) (HasSupport ?p)))

               (Proposition (healthy salad))
               (Proposition (healthy ice-cream))

               (True (healthy salad))
               (False (healthy ice-cream))
               (Be Healthy)
               (iff (Be Healthy) (not (Eat ice-cream)))

               (Source dubious-science-journal)
               (Read (healthy ice-cream) dubious-science-journal)]
  :start      []

  :avoid-if-possible [believe-without-support]

  :actions    [(define-action declare-P [?p]
                 {:preconditions [(Belief ?p)]
                  :additions     [(Declaration ?p)]
                  :deletions     [(Private ?p)]})

               (define-action believe-with-support [?p]
                 {:preconditions [(Proposition ?p)
                                  (HasSupport ?p)]
                  :additions     [(Belief ?p)]
                  :deletions     []})

               (define-action add-support [?p]
                 {:preconditions [(Proposition ?p)
                                  (Read ?p ?source)
                                  (Trusted ?source)]
                  :additions     [(HasSupport ?p)]
                  :deletions     []})

               (define-action assume-trust [?source]
                 {:preconditions [(Source ?source)]
                  :additions     [(Trusted ?source)]
                  :deletions     []})

               (define-action believe-without-support [?p]
                 {:preconditions [(Proposition ?p)]
                  :additions     [(Belief ?p)]
                  :deletions     []})

               (define-action eat [?p]
                 {:preconditions [(Belief (healthy ?p))]
                  :additions     [(Eat ?p)]
                  :deletions     []})]}

 :avoid-if-possible [believe-without-support]
 :goals {G1 {:priority    1.0
             :description "Be Healthy"
             :state       [(Be Healthy)]}


         G2 {:priority    2.0
             :description "Eat Ice Cream"
             :state       [(Eat ice-cream)]}}}
