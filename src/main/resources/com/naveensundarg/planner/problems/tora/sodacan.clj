{:name       "soda can challenge"
 :background [(forall [?thing] (if (and (Checked ?thing) (Possible ?thing))
                                  (exists ?answer (Know ?answer))))]
 :start      [(Possible A)
              (Possible B)
              (Possible C)

              (forall [?u] (if (and (not (= ?u A)) (not (= ?u B)) (not (= ?u C))) (not (Possible ?u))))]

 :actions    [(define-action checkU [?q]
                {:preconditions [(Possible ?q)]
                 :additions     [(Checked ?q)]
                 :deletions     []})]

 :goal [(exists ?answer (Know ?answer))]}