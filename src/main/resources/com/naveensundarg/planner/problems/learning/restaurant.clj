

{:name       "toy restaurant example"
 :background [ ]
 :start      [(Believes I (Believes other (= ?something  (phone R))))]

 :actions    [(define-action call [?entity]
                 {:preconditions [(Believes I (= ?num  (phone ?entity)))]
                  :additions     [(called ?entity)]
                  :deletions     [(not (called ?entity))]})

              (define-action query [?value]
                {:preconditions [(Believes I (Believes ?other (= ?something ?value)))]
                 :additions     [(Believes I (= ?something ?value))]
                 :deletions     [(not (Believes I (= ?something ?value)))]})]


 :goal [(called ?establishment)]}






