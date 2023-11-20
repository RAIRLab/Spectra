; Original problem from Joerg Hoffmann and Ronen Brafman
{:name "Block-Conformant-Tiny"
    :background [
        ; Setting object types
        (Believes! a t0 (block b1))
        (Believes! a t0 (block b2))

        ; Unique name axioms
        (Believes! a t0 (not (= b1 b2)))

        ; Block World Axioms

        ; Blocks are never on themselves
        (Believes! a t0 (forall [x] (not (on x x))))
        ; on is not symmetric
        (Believes! a t0 (forall [x y] (if (on x y) (not (on y x)))))
        ; Any block on a table isn't on top of another block
        (Believes! a t0 (forall [x y] (if (on-table x) (not (on x y)))))
        ; Any block that is cleared does not have another block on top of it
        (Believes! a t0 (forall [x y] (if (clear x) (not (on y x)))))
    ]


    :actions [

        (define-action move-t-to-b [?bm ?bt] {
            :preconditions [
                (Believes! a ?now (and
                    ; Type Restrictions
                    (block ?bm)
                    (block ?bt)
                    ; Arguments Unique
                    (not (= ?bm ?bt))
                    ; Primary preconditions
                    (clear ?bm)
                    (clear ?bt)
                    (on-table ?bm)
                ))
                ; NOTE: QA Algorithm is very barebones,
                ; currently does not support beliefs under
                ; binary operations. Example:
                ;; (and
                ;;     (Believes! a t0 (block ?bm))
                ;;     (Believes! a t0 (block ?bt))
                ;; )
            ]
            :additions [
                ; ShadowProver uses string comparisons to determine 
                ; ordering on time points.
                ; Spectra currently hacks around this by replacing
                ; ?next where the constant
                ; that represents ?now + 1.
                ; ShadowProver Limitation: Cannot go beyond 10 time points
                (Believes! a ?next (on ?bm ?bt))

                ; These below shouldn't be needed but left for posterity
                ;; (Believes! a ?next (not (clear ?bt)))
                ;; (Believes! a ?next (not (on-table ?bm)))
            ]
            :deletions [ ]
        })
    ]
    :start [
        ; Unknown facts don't need to be stated 
        ; since we don't assume closed world assumption.

        ; Negated predicates in this example is handled by
        ; the block world axioms

        (Believes! a t0 (on-table b2))
        (Believes! a t0 (on-table b1))
        (Believes! a t0 (clear b1))
        (Believes! a t0 (clear b2))
    ]
    :goal [
        ;; (Believes! a t0 (clear ?bm))
        ; Try a there exists at some point
        (exists [t] (Believes! a t (on b1 b2)))
    ]

}
