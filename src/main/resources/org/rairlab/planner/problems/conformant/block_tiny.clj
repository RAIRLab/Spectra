; Original problem from Joerg Hoffmann and Ronen Brafman
{:name "Block-Conformant-Tiny"
    :background [
        ; Setting object types
        (block b1)
        (block b2)

        ; Unique name axioms
        (not (= b1 b2))

        ; Block World Axioms

        ; Blocks are never on themselves
        (forall [x] (not (on x x)))
        ; on is not symmetric
        (forall [x y] (if (on x y) (not (on y x))))
        ; Any block on a table isn't on top of another block
        (forall [x y] (if (on-table x) (not (on x y))))
        ; Any block that is cleared does not have another block on top of it
        (forall [x y] (if (clear x) (not (on y x))))

        ; NOTE: Slow if we use complicated definitions
        ;; ; A block is on the table if it isn't on top of any other block
        ;; (forall [x] (iff (on-table x) (forall [y] (not (on x y)))))
        ;; ; A block is cleared if there is no other block on top of it
        ;; (forall [x] (iff (clear x) (forall [y] (not (on y x)))))
    ]


    :actions [

        (define-action move-bstack-to-t [?b ?b1] {
            :preconditions [ 
                ; Type restriction
                (block ?b)
                (block ?b1)
                ; Arguments unique
                (not (= ?b ?b1))

                ; Preconditions
                ;; (not (on-table ?b))

            ]
            ; TODO: Think hard about the effect
            ;;   :effect (and (when (on ?b ?bl) 
            ;;         (and (not (on ?b ?bl)) (on-table ?b) (clear ?bl)))))
            :additions [
                ; The following creates a contradiction because
                ; (on-table ?b) -> (not (on ?b ?b1)) and
                ; we can't have P -> \neg P
                ;; (if (on ?b ?b1) (and (on-table ?b) (clear ?b1))) 

            ]
            :deletions [ ]
        })

        (define-action move-t-to-b [?bm ?bt ?t] {
            :preconditions [
                ; Type restrictions
                (block ?bm)
                (block ?bt)
                ; Arguments unique
                (not (= ?bm ?bt))
                ; Primary preconditions
                (clear ?bm ?t)
                (clear ?bt ?t)
                (on-table ?bm ?t)
            ]
            :additions [
                (on ?bm ?bt (s ?t))

                (not (clear ?bt (s ?t)))
                (not (on-table ?bm (s ?t)))
            ]
            :deletions [
                ;; (not (on ?bm ?bt))
                
                ;; (clear ?bt)
                ;; (on-table ?bm)
            ]
        })
    ]
    :start [
        ; Unknown facts don't need to be stated 
        ; since we don't assume closed world assumption.

        ; Negated predicates in this example is handled by
        ; the block world axioms

        (or
            (and 
                (on b2 b1 t0)
                (clear b2 t0)
                (on-table b1 t0)
            )

            (and
                (on b1 b2 t0)
                (clear b1 t0)
                (on-table b2 t0)
            )
        )
    ]
    :goal [
        (exists [x] (on b2 b1 x))
    ]

}
