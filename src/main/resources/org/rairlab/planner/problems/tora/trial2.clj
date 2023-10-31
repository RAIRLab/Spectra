{:name       "soda can challenge 2"
 :background [;; Checks if something is an element of a list
               (forall [?thing ?w ?list] (iff (Elem ?thing ?list) (Elem ?thing (Cons ?w ?list))))
               (forall [?thing] (Elem ?thing (Cons ?thing el)))
               ;; Removes an element from a list
               (forall [?u ?l1 ?l2]
                       (if
                         (and
                          (not (Elem ?u ?l2))
                          (forall [?x]
                                  (if
                                    (Elem ?x ?l1)
                                    (or (= ?x ?u) (Elem ?x ?l2)))))
                         (Removed ?u ?l1 ?l2)))]

 :start      [(Cons a (Cons b (Cons f el)))]

 :goal       [(Cons a el)
              (Cons b el)
              (Cons f el)]

 :actions    [;; Checkbehind an object
               (define-action attend [?q ?l]
                 {:preconditions [(Elem ?q ?l)
                                  (Removed ?q ?l ?l2)]
                  :additions     [(Cons ?q el)]
                  :deletions    []})]}
