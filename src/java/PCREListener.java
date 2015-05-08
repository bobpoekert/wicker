// Generated from PCRE.g4 by ANTLR 4.4
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PCREParser}.
 */
public interface PCREListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PCREParser#backreference_or_octal}.
	 * @param ctx the parse tree
	 */
	void enterBackreference_or_octal(@NotNull PCREParser.Backreference_or_octalContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#backreference_or_octal}.
	 * @param ctx the parse tree
	 */
	void exitBackreference_or_octal(@NotNull PCREParser.Backreference_or_octalContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#octal_char}.
	 * @param ctx the parse tree
	 */
	void enterOctal_char(@NotNull PCREParser.Octal_charContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#octal_char}.
	 * @param ctx the parse tree
	 */
	void exitOctal_char(@NotNull PCREParser.Octal_charContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#quantifier_type}.
	 * @param ctx the parse tree
	 */
	void enterQuantifier_type(@NotNull PCREParser.Quantifier_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#quantifier_type}.
	 * @param ctx the parse tree
	 */
	void exitQuantifier_type(@NotNull PCREParser.Quantifier_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#callout}.
	 * @param ctx the parse tree
	 */
	void enterCallout(@NotNull PCREParser.CalloutContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#callout}.
	 * @param ctx the parse tree
	 */
	void exitCallout(@NotNull PCREParser.CalloutContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#shared_atom}.
	 * @param ctx the parse tree
	 */
	void enterShared_atom(@NotNull PCREParser.Shared_atomContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#shared_atom}.
	 * @param ctx the parse tree
	 */
	void exitShared_atom(@NotNull PCREParser.Shared_atomContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#conditional}.
	 * @param ctx the parse tree
	 */
	void enterConditional(@NotNull PCREParser.ConditionalContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#conditional}.
	 * @param ctx the parse tree
	 */
	void exitConditional(@NotNull PCREParser.ConditionalContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#character_class}.
	 * @param ctx the parse tree
	 */
	void enterCharacter_class(@NotNull PCREParser.Character_classContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#character_class}.
	 * @param ctx the parse tree
	 */
	void exitCharacter_class(@NotNull PCREParser.Character_classContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(@NotNull PCREParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(@NotNull PCREParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(@NotNull PCREParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(@NotNull PCREParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#option_flags}.
	 * @param ctx the parse tree
	 */
	void enterOption_flags(@NotNull PCREParser.Option_flagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#option_flags}.
	 * @param ctx the parse tree
	 */
	void exitOption_flags(@NotNull PCREParser.Option_flagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#non_capture}.
	 * @param ctx the parse tree
	 */
	void enterNon_capture(@NotNull PCREParser.Non_captureContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#non_capture}.
	 * @param ctx the parse tree
	 */
	void exitNon_capture(@NotNull PCREParser.Non_captureContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#alternation}.
	 * @param ctx the parse tree
	 */
	void enterAlternation(@NotNull PCREParser.AlternationContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#alternation}.
	 * @param ctx the parse tree
	 */
	void exitAlternation(@NotNull PCREParser.AlternationContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(@NotNull PCREParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(@NotNull PCREParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#alpha_nums}.
	 * @param ctx the parse tree
	 */
	void enterAlpha_nums(@NotNull PCREParser.Alpha_numsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#alpha_nums}.
	 * @param ctx the parse tree
	 */
	void exitAlpha_nums(@NotNull PCREParser.Alpha_numsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#cc_literal}.
	 * @param ctx the parse tree
	 */
	void enterCc_literal(@NotNull PCREParser.Cc_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#cc_literal}.
	 * @param ctx the parse tree
	 */
	void exitCc_literal(@NotNull PCREParser.Cc_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(@NotNull PCREParser.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(@NotNull PCREParser.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#digit}.
	 * @param ctx the parse tree
	 */
	void enterDigit(@NotNull PCREParser.DigitContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#digit}.
	 * @param ctx the parse tree
	 */
	void exitDigit(@NotNull PCREParser.DigitContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#backtrack_control}.
	 * @param ctx the parse tree
	 */
	void enterBacktrack_control(@NotNull PCREParser.Backtrack_controlContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#backtrack_control}.
	 * @param ctx the parse tree
	 */
	void exitBacktrack_control(@NotNull PCREParser.Backtrack_controlContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#newline_convention}.
	 * @param ctx the parse tree
	 */
	void enterNewline_convention(@NotNull PCREParser.Newline_conventionContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#newline_convention}.
	 * @param ctx the parse tree
	 */
	void exitNewline_convention(@NotNull PCREParser.Newline_conventionContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#look_around}.
	 * @param ctx the parse tree
	 */
	void enterLook_around(@NotNull PCREParser.Look_aroundContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#look_around}.
	 * @param ctx the parse tree
	 */
	void exitLook_around(@NotNull PCREParser.Look_aroundContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#non_close_paren}.
	 * @param ctx the parse tree
	 */
	void enterNon_close_paren(@NotNull PCREParser.Non_close_parenContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#non_close_paren}.
	 * @param ctx the parse tree
	 */
	void exitNon_close_paren(@NotNull PCREParser.Non_close_parenContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#capture}.
	 * @param ctx the parse tree
	 */
	void enterCapture(@NotNull PCREParser.CaptureContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#capture}.
	 * @param ctx the parse tree
	 */
	void exitCapture(@NotNull PCREParser.CaptureContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#option_flag}.
	 * @param ctx the parse tree
	 */
	void enterOption_flag(@NotNull PCREParser.Option_flagContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#option_flag}.
	 * @param ctx the parse tree
	 */
	void exitOption_flag(@NotNull PCREParser.Option_flagContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#parse}.
	 * @param ctx the parse tree
	 */
	void enterParse(@NotNull PCREParser.ParseContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#parse}.
	 * @param ctx the parse tree
	 */
	void exitParse(@NotNull PCREParser.ParseContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#quantifier}.
	 * @param ctx the parse tree
	 */
	void enterQuantifier(@NotNull PCREParser.QuantifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#quantifier}.
	 * @param ctx the parse tree
	 */
	void exitQuantifier(@NotNull PCREParser.QuantifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#octal_digit}.
	 * @param ctx the parse tree
	 */
	void enterOctal_digit(@NotNull PCREParser.Octal_digitContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#octal_digit}.
	 * @param ctx the parse tree
	 */
	void exitOctal_digit(@NotNull PCREParser.Octal_digitContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#subroutine_reference}.
	 * @param ctx the parse tree
	 */
	void enterSubroutine_reference(@NotNull PCREParser.Subroutine_referenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#subroutine_reference}.
	 * @param ctx the parse tree
	 */
	void exitSubroutine_reference(@NotNull PCREParser.Subroutine_referenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#non_close_parens}.
	 * @param ctx the parse tree
	 */
	void enterNon_close_parens(@NotNull PCREParser.Non_close_parensContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#non_close_parens}.
	 * @param ctx the parse tree
	 */
	void exitNon_close_parens(@NotNull PCREParser.Non_close_parensContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#cc_atom}.
	 * @param ctx the parse tree
	 */
	void enterCc_atom(@NotNull PCREParser.Cc_atomContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#cc_atom}.
	 * @param ctx the parse tree
	 */
	void exitCc_atom(@NotNull PCREParser.Cc_atomContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#backreference}.
	 * @param ctx the parse tree
	 */
	void enterBackreference(@NotNull PCREParser.BackreferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#backreference}.
	 * @param ctx the parse tree
	 */
	void exitBackreference(@NotNull PCREParser.BackreferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#letter}.
	 * @param ctx the parse tree
	 */
	void enterLetter(@NotNull PCREParser.LetterContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#letter}.
	 * @param ctx the parse tree
	 */
	void exitLetter(@NotNull PCREParser.LetterContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(@NotNull PCREParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(@NotNull PCREParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(@NotNull PCREParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(@NotNull PCREParser.CommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#digits}.
	 * @param ctx the parse tree
	 */
	void enterDigits(@NotNull PCREParser.DigitsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#digits}.
	 * @param ctx the parse tree
	 */
	void exitDigits(@NotNull PCREParser.DigitsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(@NotNull PCREParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(@NotNull PCREParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#shared_literal}.
	 * @param ctx the parse tree
	 */
	void enterShared_literal(@NotNull PCREParser.Shared_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#shared_literal}.
	 * @param ctx the parse tree
	 */
	void exitShared_literal(@NotNull PCREParser.Shared_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link PCREParser#option}.
	 * @param ctx the parse tree
	 */
	void enterOption(@NotNull PCREParser.OptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link PCREParser#option}.
	 * @param ctx the parse tree
	 */
	void exitOption(@NotNull PCREParser.OptionContext ctx);
}