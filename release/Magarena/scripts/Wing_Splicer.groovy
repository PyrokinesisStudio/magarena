[
    new MagicStatic(
            MagicLayer.Ability, 
            MagicTargetFilter.TARGET_GOLEM_YOU_CONTROL) {
        @Override
        public void modAbilityFlags(final MagicPermanent source,final MagicPermanent permanent,final Set<MagicAbility> flags) {
            flags.add(MagicAbility.Flying);
        }
    },
    Blade_Splicer.T
]
