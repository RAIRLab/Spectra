{:name           "test 1"
 :background     [p]
 :start          [q]
 :goal           [r]
 :actions        [(define-action a1 ()
                   {:preconditions [(or q r)]
                    :additions     [r]
                    :deletions     [q]})]

 :expected-plans ([a1])}


{:name           "simple killing"
 :background     []
 :start          [(forall ?x (alive ?x))]
 :goal           [(forall ?x (dead ?x))]
 :actions
 [(define-action kill ()
    {:preconditions [(alive ?x)]
     :additions     [(dead ?x)]
     :deletions     [(alive ?x)]})]

 :expected-plans ([kill])}


{:name           "thirsty"
 :background     []
 :start          [thirsty]
 :goal           [(not thirsty)]
 :actions
 [(define-action drink ()
    {:preconditions [thirsty]
     :additions     [(not thirsty)]
     :deletions     [thirsty]})
  (define-action eat ()
    {:preconditions [hungry]
     :additions     [(not hungry)]
     :deletions     [hungry]})]

 :expected-plans ([drink])}


{:name           "hungry"
 :background     []
 :start          [hungry]
 :goal           [(not hungry)]
 :actions

 [(define-action drink ()
    {:preconditions [thirsty]
     :additions     [(not thirsty)]
     :deletions     [thirsty]})

  (define-action eat ()
    {:preconditions [hungry]
     :additions     [(not hungry)]
     :deletions     [hungry]})]

 :expected-plans ([eat])}


{:name           "hungry and thirsty"
 :background     []
 :start          [hungry thirsty]
 :goal           [(not (or hungry thirsty))]
 :actions
 [(define-action drink ()
    {:preconditions [thirsty]
     :additions     [(not thirsty)]
     :deletions     [thirsty]})

  (define-action eat ()
    {:preconditions [hungry]
     :additions     [(not hungry)]
     :deletions     [hungry]})]

 :expected-plans ([eat drink]
                   [drink eat])}

{:name           "hungry and thirsty"
 :background     []
 :start          [hungry thirsty work-unfinished]
 :goal           [work-finished]
 :actions

 [(define-action drink ()
    {:preconditions [thirsty]
     :additions     [(not thirsty)]
     :deletions     [thirsty]})

  (define-action eat ()
    {:preconditions [hungry]
     :additions     [(not hungry)]
     :deletions     [hungry]})

  (define-action work ()
    {:preconditions [(and (not hungry) (not thirsty))]
     :additions     [work-finished]
     :deletions     [work-unfinished]})]

 :expected-plans ([eat drink work]
                   [drink eat work])}

{:name           "Heinz Dilemma"
 :background     [(cures radium-drug-x cancerx)]
 :start          [(sick (wife heinz) cancerx)
                  (= 5000 (cost radium-drug-x))
                  (not (can-spend 5000))
                  (not (possess radium-drug-x))]
 :goal           [(healthy (wife heinz))]
 :actions

 [(define-action administer-medicine [?medicine ?condition ?person]
    {:preconditions [(sick ?person ?condition)
                     (cures ?medicine ?condition)
                     (possess ?medicine)]
     :additions     [(healthy ?person)]
     :deletions     [(sick ?person ?condition)]})

  (define-action buy-medicine [?medicine]
    {:preconditions [(can-spend (cost ?medicine))]
     :additions     [(possess ?medicine)]
     :deletions     [(not (possess ?medicine))]})


  (define-action steal-medicine [?medicine]
    {:preconditions [(not (possess ?medicine))]
     :additions     [(possess ?medicine)]
     :deletions     [(not (possess ?medicine))]})


  (define-action finance [?amount]
    {:preconditions [(not (can-spend ?amount))]
     :additions     [(can-spend ?amount)]
     :deletions     [(not (can-spend ?amount))]})]

 :expected-plans ([buy-medicine administer-medicine])}


{:name           "belief intro"
 :background     [(Proposition god-exists)
                  (forall [?p] (if (Proposition ?p) (or (True ?p) (False ?p))))
                  (forall [?p] (iff (True ?p) (not (False ?p))))
                  (forall [?p] (iff (True ?p) (HasSupport ?p)))
                  (False god-exists)]
 :start          []
 :goal           [(Declaration god-exists)]
 :actions
                 [(define-action declare-P [?p]
                                 {:preconditions [(Belief ?p)]
                                  :additions     [(Declaration ?p)]
                                  :deletions     [(Private ?p)]})

                  (define-action believe-with-support [?p]
                                 {:preconditions [(Proposition ?p)
                                                  (HasSupport ?p)]
                                  :additions     [(Belief ?p)]
                                  :deletions     []})

                  (define-action believe-without-support [?p]
                                 {:preconditions [(Proposition ?p)]
                                  :additions     [(Belief ?p)]
                                  :deletions     []})]

 :expected-plans ([believe-P declare-P])}

{:name           "reasoning 1"
 :background     []
 :start          [(! p) (!  q)]
 :goal           [(! (and p q))]

 :actions        [(define-action and-intro [?p ?q]
                    {:preconditions [(! ?p) (!  ?q)]
                     :additions     [(! (and ?p ?q))]
                     :deletions     []})
                  (define-action cond-elim [?p ?q]
                    {:preconditions [(! (if ?p ?q)) (!  ?p)]
                     :additions     [(! ?q)]
                     :deletions     []})]

 :expected-plans ([and-intro])}


{:name           "reasoning 2"
 :background     []
 :start          [(! p) (!  q)
                  (! (if (and p q) r))]
 :goal           [(! r)]

 :actions        [(define-action and-intro [?p ?q]
                    {:preconditions [(! ?p) (!  ?q)]
                     :additions     [(! (and ?p ?q))]
                     :deletions     []})
                  (define-action cond-elim [?p ?q]
                    {:preconditions [(! (if ?p ?q)) (!  ?p)]
                     :additions     [(! ?q)]
                     :deletions     []})]

 :expected-plans ([and-intro])}

;{:name           "reasoning 3"
; :background     []
; :start          [(! A) (! B)
;                  (Prop S)
;                  (! (if (and A B) C))
;                  ]
; :goal           [(! (if S C))]
;
; :actions        [(define-action and-intro [?p ?q]
;                                 {:preconditions [(! ?p) (! ?q)]
;                                  :additions     [(! (and ?p ?q))]
;                                  :deletions     []})
;                  (define-action cond-elim [?p ?q]
;                                 {:preconditions [(! (if ?p ?q)) (! ?p)]
;                                  :additions     [(! ?q)]
;                                  :deletions     []})
;
;                  (define-action cond-intro [?p ?q]
;                                 {:preconditions [(Prop ?p) (! ?q)]
;                                  :additions     [(! (if ?p ?q))]
;                                  :deletions     []})]
;
; :expected-plans ([and-intro])}
